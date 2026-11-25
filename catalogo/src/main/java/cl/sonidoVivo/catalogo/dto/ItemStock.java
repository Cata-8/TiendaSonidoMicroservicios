package cl.sonidoVivo.catalogo.dto;

// Una línea de descuento de stock: "descontar 2 unidades del producto GA001"
public record ItemStock(String codigo, Integer cantidad) {
}
