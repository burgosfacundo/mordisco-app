package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "promociones")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Promocion {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String descripcion;

    @Column (nullable = false)
    private Double descuento;

    @Column (nullable = false)
    private LocalDate fechaInicio;

    @Column (nullable = false)
    private LocalDate fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;
}
