package cl.sonidoVivo.comprasRealizadas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código del producto en el catálogo (sin FK, es otro microservicio)
    @Column(name = "producto_codigo", nullable = false, length = 20)
    private String productoCodigo;

    // Se copian el nombre y el precio del momento de la compra,
    // para que el historial no cambie si después se edita el producto.
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;
}
