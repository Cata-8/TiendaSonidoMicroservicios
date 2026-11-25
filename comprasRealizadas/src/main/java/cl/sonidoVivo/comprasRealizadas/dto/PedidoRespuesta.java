package cl.sonidoVivo.comprasRealizadas.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.sonidoVivo.comprasRealizadas.model.Pedido;

// numero, total y unidades coinciden con la interfaz Pedido que ya usa Pago.tsx
public record PedidoRespuesta(
        Long id,
        String numero,
        Long usuarioId,
        Integer total,
        Integer unidades,
        String estado,
        LocalDateTime fecha,
        List<DetalleRespuesta> items
) {
    public static PedidoRespuesta desde(Pedido p){
        return new PedidoRespuesta(
            p.getId(),
            p.getNumero(),
            p.getUsuarioId(),
            p.getTotal(),
            p.getUnidades(),
            p.getEstado(),
            p.getFechaCreacion(),
            p.getDetalles().stream().map(DetalleRespuesta::desde).toList()
        );
    }
}
