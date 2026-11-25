package cl.sonidoVivo.usuarios.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // null cuando no tiene RUN (ej: el administrador). MySQL permite varios NULL en una columna única.
    @Column(length = 12, unique = true)
    private String run;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos = "";

    @Column(nullable = false, length = 100, unique = true)
    private String correo;

    // Se guarda como texto "AAAA-MM-DD" porque así lo maneja el front
    @Column(name = "fecha_nacimiento", nullable = false, length = 10)
    private String fechaNacimiento = "";

    // "admin" o "cliente" (en minúscula, igual que el tipo Rol del React)
    @Column(nullable = false, length = 20)
    private String rol = "cliente";

    @Column(nullable = false, length = 100)
    private String region = "";

    @Column(nullable = false, length = 100)
    private String comuna = "";

    @Column(nullable = false, length = 300)
    private String direccion = "";

    // Aquí va el hash BCrypt, nunca la clave en texto plano
    @Column(nullable = false, length = 100)
    private String clave;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void asignarFechaCreacion(){
        this.fechaCreacion = LocalDateTime.now();
    }
}
