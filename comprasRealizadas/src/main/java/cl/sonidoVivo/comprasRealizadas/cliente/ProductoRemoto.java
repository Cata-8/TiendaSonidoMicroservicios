package cl.sonidoVivo.comprasRealizadas.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Lo que pedidos necesita saber de un producto del catálogo (ignora el resto de los campos)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoRemoto(String codigo, String nombre, Integer precio, Integer stock) {
}
