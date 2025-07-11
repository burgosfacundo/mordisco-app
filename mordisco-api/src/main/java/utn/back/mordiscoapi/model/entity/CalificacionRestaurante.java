package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "calificaciones_restaurante",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_calificacion_usuario_restaurante",
                columnNames = {"restaurante_id", "usuario_id"}
        )
)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CalificacionRestaurante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int puntaje;

    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}

