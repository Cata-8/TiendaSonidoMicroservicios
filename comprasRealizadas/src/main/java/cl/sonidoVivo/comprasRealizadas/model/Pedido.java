package cl.sonidoVivo.comprasRealizadas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número visible para el cliente, ej: SV-123456
    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    // Solo el id: el usuario vive en el microservicio de usuarios (no hay FK entre bases de datos).
    // null = compra sin sesión iniciada.
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false)
    private Integer unidades;

    @Column(nullable = false, length = 20)
    private String estado = "APROBADO";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id", nullable = false)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    protected void asignarFechaCreacion(){
        this.fechaCreacion = LocalDateTime.now();
    }
}
