package cl.sonidoVivo.catalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    // El código (ej: GA001) es la clave primaria, igual que en el React
    @Id
    @Column(nullable = false, length = 20)
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigo;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 80)
    private String categoria;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 80)
    private String marca;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 80)
    private String modelo;

    @Column(nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer precio;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "La imagen es obligatoria")
    @Size(max = 255)
    private String imagen;
}
