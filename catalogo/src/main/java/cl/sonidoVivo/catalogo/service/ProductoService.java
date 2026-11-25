package cl.sonidoVivo.catalogo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.catalogo.dto.ItemStock;
import cl.sonidoVivo.catalogo.model.Producto;
import cl.sonidoVivo.catalogo.repository.ProductoRepository;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository){
        this.repository = repository;
    }

    public List<Producto> listar(){
        return repository.findAll();
    }

    public Producto buscar(String codigo){
        return repository.findById(codigo)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el producto " + codigo));
    }

    public Producto guardar(Producto producto){
        producto.setCodigo(producto.getCodigo().trim());

        if (repository.existsById(producto.getCodigo())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Ya existe un producto con el código " + producto.getCodigo());
        }
        return repository.save(producto);
    }

    public Producto actualizar(String codigo, Producto datos){
        buscar(codigo); // 404 si no existe

        // El código de la URL manda; el código no se puede cambiar
        datos.setCodigo(codigo);
        return repository.save(datos);
    }

    public void eliminar(String codigo){
        buscar(codigo); // 404 si no existe
        repository.deleteById(codigo);
    }

    // Lo usa el microservicio de pedidos al confirmar una compra.
    // @Transactional: si un solo producto no alcanza, se deshace el descuento de todos.
    @Transactional
    public void descontarStock(List<ItemStock> items){
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay productos para descontar");
        }

        for (ItemStock item : items) {
            if (item.codigo() == null || item.cantidad() == null || item.cantidad() < 1) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cada ítem necesita un código y una cantidad mayor a 0");
            }

            int filas = repository.descontarStock(item.codigo(), item.cantidad());

            if (filas == 0) {
                if (!repository.existsById(item.codigo())) {
                    throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el producto " + item.codigo());
                }
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Stock insuficiente para el producto " + item.codigo());
            }
        }
    }
}
