package cl.sonidoVivo.comprasRealizadas.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

// usuarioId es opcional: el Pago del React también permite comprar sin iniciar sesión
public record PedidoSolicitud(
        Long usuarioId,
        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid List<ItemSolicitud> items
) {
}
