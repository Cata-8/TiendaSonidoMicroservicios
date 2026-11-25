package cl.sonidoVivo.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.sonidoVivo.usuarios.dto.LoginSolicitud;
import cl.sonidoVivo.usuarios.dto.UsuarioRespuesta;
import cl.sonidoVivo.usuarios.dto.UsuarioSolicitud;
import cl.sonidoVivo.usuarios.model.Usuario;
import cl.sonidoVivo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    @GetMapping
    public List<UsuarioRespuesta> listar(){
        return service.listar().stream()
            .map(UsuarioRespuesta::desde)
            .toList();
    }

    @GetMapping("/{id}")
    public UsuarioRespuesta buscar(@PathVariable("id") Long id){
        return UsuarioRespuesta.desde(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioRespuesta> crear(@Valid @RequestBody UsuarioSolicitud datos){
        Usuario creado = service.crear(datos);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UsuarioRespuesta.desde(creado));
    }

    @PutMapping("/{id}")
    public UsuarioRespuesta actualizar(@PathVariable("id") Long id,
                                       @Valid @RequestBody UsuarioSolicitud datos){
        return UsuarioRespuesta.desde(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Devuelve el usuario si correo y clave son correctos; si no, 401
    @PostMapping("/login")
    public UsuarioRespuesta iniciarSesion(@Valid @RequestBody LoginSolicitud datos){
        return UsuarioRespuesta.desde(service.iniciarSesion(datos.correo(), datos.clave()));
    }
}
