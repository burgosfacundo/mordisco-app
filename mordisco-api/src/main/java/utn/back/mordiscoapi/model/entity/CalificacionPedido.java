package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Calificación de un pedido por parte del cliente.
 * Evalúa la calidad de la comida, tiempo de entrega, packaging, etc.
 */
@Entity
@Table(
        name = "calificaciones_pedido",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_calificacion_pedido_usuario",
                columnNames = {"pedido_id", "usuario_id"}
        )
)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CalificacionPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntajeComida; // 1-5: Calidad de la comida

    @Column(nullable = false)
    private Integer puntajeTiempo; // 1-5: Tiempo de entrega

    @Column(nullable = false)
    private Integer puntajePackaging; // 1-5: Estado del empaque

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    /**
     * Calcula el puntaje promedio del pedido
     */
    public Double getPuntajePromedio() {
        return (puntajeComida + puntajeTiempo + puntajePackaging) / 3.0;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}