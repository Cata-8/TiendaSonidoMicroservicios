package cl.sonidoVivo.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginSolicitud(
        @NotBlank(message = "Debe ingresar el correo") String correo,
        @NotBlank(message = "Debe ingresar la clave") String clave
) {
}
