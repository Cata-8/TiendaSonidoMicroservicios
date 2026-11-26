package cl.sonidoVivo.usuarios.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.usuarios.dto.UsuarioSolicitud;
import cl.sonidoVivo.usuarios.model.Usuario;
import cl.sonidoVivo.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar(){
        return repository.findAll();
    }

    public Usuario buscar(Long id){
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el usuario " + id));
    }

    // esAdmin = true solo si quien hace la petición es un administrador con sesión.
    // Así nadie puede registrarse a sí mismo como admin: un registro público siempre crea un "cliente".
    public Usuario crear(UsuarioSolicitud datos, boolean esAdmin){
        if (datos.clave() == null || datos.clave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe ingresar la clave");
        }
        validarDuplicados(datos, null);

        Usuario usuario = new Usuario();
        copiarDatos(datos, usuario);
        if (!esAdmin) {
            usuario.setRol("cliente");
        }
        usuario.setClave(passwordEncoder.encode(datos.clave()));

        return repository.save(usuario);
    }

    public Usuario actualizar(Long id, UsuarioSolicitud datos){
        Usuario usuario = buscar(id);
        validarDuplicados(datos, id);
        copiarDatos(datos, usuario);

        // Si no mandan clave, se conserva la que ya tenía
        if (datos.clave() != null && !datos.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(datos.clave()));
        }
        return repository.save(usuario);
    }

    public void eliminar(Long id){
        buscar(id); // 404 si no existe
        repository.deleteById(id);
    }

    public Usuario iniciarSesion(String correo, String clave){
        return repository.findByCorreo(correo.trim())
            .filter(u -> passwordEncoder.matches(clave, u.getClave()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos"));
    }

    // idActual = null al crear; al editar es el id del usuario que se está editando
    private void validarDuplicados(UsuarioSolicitud datos, Long idActual){
        repository.findByCorreo(datos.correo().trim()).ifPresent(otro -> {
            if (!otro.getId().equals(idActual)) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una cuenta registrada con ese correo");
            }
        });

        String run = normalizarRun(datos.run());
        if (run != null) {
            repository.findByRun(run).ifPresent(otro -> {
                if (!otro.getId().equals(idActual)) {
                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Ya existe una cuenta registrada con ese RUN");
                }
            });
        }
    }

    private void copiarDatos(UsuarioSolicitud datos, Usuario usuario){
        usuario.setRun(normalizarRun(datos.run()));
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(texto(datos.apellidos()));
        usuario.setCorreo(datos.correo().trim());
        usuario.setFechaNacimiento(texto(datos.fechaNacimiento()));
        usuario.setRol(datos.rol() == null || datos.rol().isBlank() ? "cliente" : datos.rol());
        usuario.setRegion(texto(datos.region()));
        usuario.setComuna(texto(datos.comuna()));
        usuario.setDireccion(texto(datos.direccion()));
    }

    // RUN vacío se guarda como null para no chocar con la restricción de único
    private String normalizarRun(String run){
        return run == null || run.isBlank() ? null : run.trim();
    }

    private String texto(String valor){
        return valor == null ? "" : valor.trim();
    }
}
