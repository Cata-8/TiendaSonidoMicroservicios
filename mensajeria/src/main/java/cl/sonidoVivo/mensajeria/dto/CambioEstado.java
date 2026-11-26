package cl.sonidoVivo.mensajeria.dto;

import jakarta.validation.constraints.Pattern;

public record CambioEstado(
        @Pattern(regexp = "PENDIENTE|LEIDO|RESUELTO",
                 message = "El estado debe ser PENDIENTE, LEIDO o RESUELTO")
        String estado
) {
}
