package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Calificación de un repartidor por parte del cliente.
 * Evalúa la atención, comunicación y profesionalismo del repartidor.
 */
@Entity
@Table(
        name = "calificaciones_repartidor",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_calificacion_repartidor_pedido",
                columnNames = {"pedido_id", "usuario_id"}
        )
)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CalificacionRepartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntajeAtencion; // 1-5: Amabilidad y atención

    @Column(nullable = false)
    private Integer puntajeComunicacion; // 1-5: Comunicación durante la entrega

    @Column(nullable = false)
    private Integer puntajeProfesionalismo; // 1-5: Presentación y profesionalismo

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Usuario repartidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Cliente que califica

    /**
     * Calcula el puntaje promedio del repartidor
     */
    public Double getPuntajePromedio() {
        return (puntajeAtencion + puntajeComunicacion + puntajeProfesionalismo) / 3.0;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}