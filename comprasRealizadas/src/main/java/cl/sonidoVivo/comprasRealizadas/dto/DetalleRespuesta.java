package cl.sonidoVivo.comprasRealizadas.dto;

import cl.sonidoVivo.comprasRealizadas.model.DetallePedido;

public record DetalleRespuesta(
        String codigo,
        String nombre,
        Integer precioUnitario,
        Integer cantidad,
        Integer subtotal
) {
    public static DetalleRespuesta desde(DetallePedido d){
        return new DetalleRespuesta(
            d.getProductoCodigo(),
            d.getNombre(),
            d.getPrecioUnitario(),
            d.getCantidad(),
            d.getPrecioUnitario() * d.getCantidad()
        );
    }
}
