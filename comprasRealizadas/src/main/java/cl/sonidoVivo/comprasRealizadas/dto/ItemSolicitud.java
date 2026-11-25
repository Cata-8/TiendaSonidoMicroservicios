package cl.sonidoVivo.comprasRealizadas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Una línea del carrito. El precio NO viaja: lo toma el servidor desde el catálogo.
public record ItemSolicitud(
        @NotBlank(message = "Cada ítem necesita el código del producto") String codigo,
        @NotNull(message = "Cada ítem necesita una cantidad")
        @Min(value = 1, message = "La cantidad mínima es 1") Integer cantidad
) {
}
