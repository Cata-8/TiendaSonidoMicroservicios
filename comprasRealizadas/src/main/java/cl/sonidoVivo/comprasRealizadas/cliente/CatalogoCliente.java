package cl.sonidoVivo.comprasRealizadas.cliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

// Habla con el microservicio de catalogo (puerto 8083)
@Component
public class CatalogoCliente {
    private final RestClient restClient;

    public CatalogoCliente(@Value("${servicios.catalogo.url}") String url){
        this.restClient = RestClient.create(url);
    }

    public ProductoRemoto buscarProducto(String codigo){
        try {
            return restClient.get()
                .uri("/api/productos/{codigo}", codigo)
                .retrieve()
                .body(ProductoRemoto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El producto " + codigo + " no existe");
        } catch (ResourceAccessException e) {
            throw servicioCaido();
        }
    }

    public void descontarStock(List<ItemStockRemoto> items){
        try {
            restClient.post()
                .uri("/api/productos/descontar-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .body(items)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Stock insuficiente para completar la compra");
        } catch (ResourceAccessException e) {
            throw servicioCaido();
        }
    }

    private ResponseStatusException servicioCaido(){
        return new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "El servicio de catálogo no está disponible");
    }
}
