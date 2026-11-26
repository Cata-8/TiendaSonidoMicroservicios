package cl.sonidoVivo.catalogo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.sonidoVivo.catalogo.dto.ItemStock;
import cl.sonidoVivo.catalogo.model.Producto;
import cl.sonidoVivo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos (lectura pública; escritura solo ADMIN)")
public class ProductoController {
    private final ProductoService service;
    private final String claveServicio;

    public ProductoController(ProductoService service,
                              @Value("${seguridad.clave-servicio}") String claveServicio){
        this.service = service;
        this.claveServicio = claveServicio;
    }

    @Operation(summary = "Listar productos (público)")
    @GetMapping
    public List<Producto> listar(){
        return service.listar();
    }

    @Operation(summary = "Buscar un producto por código (público)")
    @GetMapping("/{codigo}")
    public Producto buscar(@PathVariable("codigo") String codigo){
        return service.buscar(codigo);
    }

    @Operation(summary = "Crear producto (solo ADMIN)", description = "409 si el código ya existe")
    @PostMapping
    public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto producto){
        Producto guardado = service.guardar(producto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(guardado);
    }

    @Operation(summary = "Actualizar producto (solo ADMIN)")
    @PutMapping("/{codigo}")
    public Producto actualizar(@PathVariable("codigo") String codigo,
                               @Valid @RequestBody Producto producto){
        return service.actualizar(codigo, producto);
    }

    @Operation(summary = "Eliminar producto (solo ADMIN)")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable("codigo") String codigo){
        service.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    // Body: [{"codigo":"GA001","cantidad":2}, ...]
    // Solo lo usa el microservicio de compras: debe enviar la clave interna en X-Clave-Servicio.
    // Así un cliente con sesión no puede vaciar el stock llamando directamente a este endpoint.
    @Operation(summary = "Descontar stock (uso interno de comprasRealizadas)",
               description = "Requiere la cabecera X-Clave-Servicio. 409 si no alcanza el stock.")
    @PostMapping("/descontar-stock")
    public ResponseEntity<Void> descontarStock(
            @RequestHeader(value = "X-Clave-Servicio", required = false) String clave,
            @RequestBody List<ItemStock> items){
        if (clave == null || !clave.equals(claveServicio)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el servicio de compras puede descontar stock");
        }
        service.descontarStock(items);
        return ResponseEntity.noContent().build();
    }
}
