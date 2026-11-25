package cl.sonidoVivo.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos que envía el front para crear o editar un usuario.
// La clave es opcional al editar: si viene vacía se conserva la actual.
public record UsuarioSolicitud(
        @Size(max = 12, message = "El RUN no puede superar los 12 caracteres")
        String run,

        @NotBlank(message = "Debe ingresar el nombre")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @NotBlank(message = "Debe ingresar el correo")
        @Email(message = "Ingrese un correo válido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @Size(max = 10)
        String fechaNacimiento,

        @Pattern(regexp = "admin|cliente", message = "El tipo de usuario debe ser admin o cliente")
        String rol,

        @Size(max = 100) String region,
        @Size(max = 100) String comuna,
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String direccion,

        @Size(min = 4, max = 50, message = "La clave debe tener entre 4 y 50 caracteres")
        String clave
) {
}
