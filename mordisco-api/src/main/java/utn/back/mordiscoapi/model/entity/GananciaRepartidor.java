package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ganancias_repartidor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GananciaRepartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Usuario repartidor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoDelivery;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal comisionPlataforma;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal gananciaRepartidor;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAplicado;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }
}
