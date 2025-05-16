package utn.back.mordiscoapi.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "promociones")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Promocion {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY) //primary key auto_increments
    private Long id;
    private String descripcion;
    @Column (nullable = false)
    private Double descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
