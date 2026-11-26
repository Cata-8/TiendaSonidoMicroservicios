package cl.sonidoVivo.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.usuarios.dto.LoginRespuesta;
import cl.sonidoVivo.usuarios.dto.LoginSolicitud;
import cl.sonidoVivo.usuarios.dto.UsuarioRespuesta;
import cl.sonidoVivo.usuarios.dto.UsuarioSolicitud;
import cl.sonidoVivo.usuarios.model.Usuario;
import cl.sonidoVivo.usuarios.service.JwtServicio;
import cl.sonidoVivo.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Registro, inicio de sesión (JWT) y mantenedor de usuarios")
public class UsuarioController {
    private final UsuarioService service;
    private final JwtServicio jwtServicio;

    public UsuarioController(UsuarioService service, JwtServicio jwtServicio){
        this.service = service;
        this.jwtServicio = jwtServicio;
    }

    @Operation(summary = "Listar todos los usuarios (solo ADMIN)")
    @GetMapping
    public List<UsuarioRespuesta> listar(){
        return service.listar().stream()
            .map(UsuarioRespuesta::desde)
            .toList();
    }

    @Operation(summary = "Buscar un usuario por id (ADMIN, o el propio usuario)")
    @GetMapping("/{id}")
    public UsuarioRespuesta buscar(@PathVariable("id") Long id, Authentication auth){
        if (!esAdmin(auth) && !auth.getName().equals(String.valueOf(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver los datos de otro usuario");
        }
        return UsuarioRespuesta.desde(service.buscar(id));
    }

    @Operation(summary = "Crear usuario / registrarse (público; solo un ADMIN puede crear otro admin)")
    @PostMapping
    public ResponseEntity<UsuarioRespuesta> crear(@Valid @RequestBody UsuarioSolicitud datos,
                                                  Authentication auth){
        Usuario creado = service.crear(datos, esAdmin(auth));

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UsuarioRespuesta.desde(creado));
    }

    @Operation(summary = "Actualizar usuario (solo ADMIN)")
    @PutMapping("/{id}")
    public UsuarioRespuesta actualizar(@PathVariable("id") Long id,
                                       @Valid @RequestBody UsuarioSolicitud datos){
        return UsuarioRespuesta.desde(service.actualizar(id, datos));
    }

    @Operation(summary = "Eliminar usuario (solo ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Iniciar sesión: devuelve el token JWT y los datos del usuario (público)",
               description = "Responde 401 si el correo o la clave son incorrectos.")
    @PostMapping("/login")
    public LoginRespuesta iniciarSesion(@Valid @RequestBody LoginSolicitud datos){
        Usuario usuario = service.iniciarSesion(datos.correo(), datos.clave());
        return new LoginRespuesta(jwtServicio.generar(usuario), UsuarioRespuesta.desde(usuario));
    }

    // auth es null cuando la petición viene sin token (visitante)
    private boolean esAdmin(Authentication auth){
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
