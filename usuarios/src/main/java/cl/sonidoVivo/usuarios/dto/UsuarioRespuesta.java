package cl.sonidoVivo.usuarios.dto;

import cl.sonidoVivo.usuarios.model.Usuario;

// Lo que se devuelve al front. Tiene los mismos campos que el tipo Usuario del React,
// pero SIN la clave (el hash nunca sale del microservicio).
public record UsuarioRespuesta(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        String fechaNacimiento,
        String rol,
        String region,
        String comuna,
        String direccion
) {
    public static UsuarioRespuesta desde(Usuario u){
        return new UsuarioRespuesta(
            u.getId(),
            u.getRun() == null ? "" : u.getRun(),
            u.getNombre(),
            u.getApellidos(),
            u.getCorreo(),
            u.getFechaNacimiento(),
            u.getRol(),
            u.getRegion(),
            u.getComuna(),
            u.getDireccion()
        );
    }
}
