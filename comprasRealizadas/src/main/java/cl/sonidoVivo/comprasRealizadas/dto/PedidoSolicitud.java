package cl.sonidoVivo.comprasRealizadas.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

// El comprador NO viaja en el cuerpo: el servidor lo toma del token JWT (así nadie compra "a nombre de otro").
public record PedidoSolicitud(
        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid List<ItemSolicitud> items
) {
}
