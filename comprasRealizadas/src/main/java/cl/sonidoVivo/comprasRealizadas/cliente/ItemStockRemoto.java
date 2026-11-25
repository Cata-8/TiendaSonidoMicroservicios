package cl.sonidoVivo.comprasRealizadas.cliente;

// Se envía a catalogo: POST /api/productos/descontar-stock
public record ItemStockRemoto(String codigo, Integer cantidad) {
}
