package cl.sonidoVivo.comprasRealizadas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.sonidoVivo.comprasRealizadas.dto.PedidoRespuesta;
import cl.sonidoVivo.comprasRealizadas.dto.PedidoSolicitud;
import cl.sonidoVivo.comprasRealizadas.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoRespuesta> crear(@Valid @RequestBody PedidoSolicitud solicitud){
        PedidoRespuesta creado = service.crear(solicitud);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(creado);
    }

    // GET /api/pedidos            -> todos (panel admin)
    // GET /api/pedidos?usuarioId=2 -> los de un cliente
    @GetMapping
    public List<PedidoRespuesta> listar(
            @RequestParam(name = "usuarioId", required = false) Long usuarioId){
        return service.listar(usuarioId);
    }

    @GetMapping("/{id}")
    public PedidoRespuesta buscar(@PathVariable("id") Long id){
        return service.buscar(id);
    }
}
