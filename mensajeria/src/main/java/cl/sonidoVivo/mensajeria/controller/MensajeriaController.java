package cl.sonidoVivo.mensajeria.controller;

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

import cl.sonidoVivo.mensajeria.dto.CambioEstado;
import cl.sonidoVivo.mensajeria.model.Mensajeria;
import cl.sonidoVivo.mensajeria.service.MensajeriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contactos")
@Tag(name = "Contactos", description = "Mensajes del formulario de contacto (enviar es público; el resto solo ADMIN)")
public class MensajeriaController {
    private final MensajeriaService service;

    public MensajeriaController(MensajeriaService service){
        this.service = service;
    }

    @Operation(summary = "Enviar un mensaje de contacto (público)")
    @PostMapping 
    public ResponseEntity<Mensajeria> guardar(@RequestBody Mensajeria contacto){
        Mensajeria guardado = service.guardar(contacto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(guardado);
    }

    @Operation(summary = "Listar los mensajes recibidos (solo ADMIN)")
    @GetMapping 
    public List<Mensajeria> listar(){
        return service.listar();
    }

    @Operation(summary = "Buscar un mensaje por id (solo ADMIN)")
    @GetMapping("/{id}")
    public Mensajeria buscar(@PathVariable("id") Long id){
        return service.buscar(id);
    }

    @Operation(summary = "Cambiar el estado: PENDIENTE, LEIDO o RESUELTO (solo ADMIN)")
    @PutMapping("/{id}/estado")
    public Mensajeria cambiarEstado(@PathVariable("id") Long id,
                                    @Valid @RequestBody CambioEstado cambio){
        return service.cambiarEstado(id, cambio.estado());
    }

    @Operation(summary = "Eliminar un mensaje (solo ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
