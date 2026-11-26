package cl.sonidoVivo.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Lo que recibe el front al iniciar sesión: el token JWT y los datos del usuario (sin clave)
public record LoginRespuesta(
        @Schema(description = "Token JWT para enviar en la cabecera Authorization: Bearer <token>")
        String token,
        UsuarioRespuesta usuario
) {
}
