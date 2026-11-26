package cl.sonidoVivo.comprasRealizadas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.comprasRealizadas.dto.PedidoRespuesta;
import cl.sonidoVivo.comprasRealizadas.dto.PedidoSolicitud;
import cl.sonidoVivo.comprasRealizadas.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Compras realizadas. Requiere sesión; un cliente solo ve sus propios pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service){
        this.service = service;
    }

    @Operation(summary = "Crear un pedido a nombre del usuario del token",
               description = "El precio y el total los calcula el servidor. 409 si no hay stock suficiente.")
    @PostMapping
    public ResponseEntity<PedidoRespuesta> crear(@Valid @RequestBody PedidoSolicitud solicitud,
                                                 Authentication auth){
        PedidoRespuesta creado = service.crear(idDelToken(auth), solicitud);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(creado);
    }

    // ADMIN: GET /api/pedidos (todos) o ?usuarioId=2 (los de un cliente)
    // CLIENTE: siempre recibe solo los suyos, aunque pida otro usuarioId
    @Operation(summary = "Listar pedidos (ADMIN: todos o por usuarioId; CLIENTE: solo los suyos)")
    @GetMapping
    public List<PedidoRespuesta> listar(
            @RequestParam(name = "usuarioId", required = false) Long usuarioId,
            Authentication auth){
        Long filtro = esAdmin(auth) ? usuarioId : idDelToken(auth);
        return service.listar(filtro);
    }

    @Operation(summary = "Buscar un pedido por id (ADMIN o el dueño del pedido)")
    @GetMapping("/{id}")
    public PedidoRespuesta buscar(@PathVariable("id") Long id, Authentication auth){
        PedidoRespuesta pedido = service.buscar(id);

        if (!esAdmin(auth) && !idDelToken(auth).equals(pedido.usuarioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ese pedido pertenece a otro usuario");
        }
        return pedido;
    }

    // En el token, "sub" es el id del usuario
    private Long idDelToken(Authentication auth){
        return Long.valueOf(auth.getName());
    }

    private boolean esAdmin(Authentication auth){
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
