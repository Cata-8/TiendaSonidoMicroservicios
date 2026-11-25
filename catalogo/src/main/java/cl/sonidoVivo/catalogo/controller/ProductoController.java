package cl.sonidoVivo.catalogo.controller;

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

import cl.sonidoVivo.catalogo.dto.ItemStock;
import cl.sonidoVivo.catalogo.model.Producto;
import cl.sonidoVivo.catalogo.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;

    public ProductoController(ProductoService service){
        this.service = service;
    }

    @GetMapping
    public List<Producto> listar(){
        return service.listar();
    }

    @GetMapping("/{codigo}")
    public Producto buscar(@PathVariable("codigo") String codigo){
        return service.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto producto){
        Producto guardado = service.guardar(producto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(guardado);
    }

    @PutMapping("/{codigo}")
    public Producto actualizar(@PathVariable("codigo") String codigo,
                               @Valid @RequestBody Producto producto){
        return service.actualizar(codigo, producto);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable("codigo") String codigo){
        service.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    // Body: [{"codigo":"GA001","cantidad":2}, ...]
    @PostMapping("/descontar-stock")
    public ResponseEntity<Void> descontarStock(@RequestBody List<ItemStock> items){
        service.descontarStock(items);
        return ResponseEntity.noContent().build();
    }
}
