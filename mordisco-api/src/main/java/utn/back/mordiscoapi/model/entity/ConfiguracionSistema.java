package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_sistema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal porcentajeGananciasRestaurante;

    @Column(nullable = false)
    private BigDecimal radioMaximoEntrega;

    @Column(nullable = false)
    private Integer tiempoMaximoEntrega;

    @Column(nullable = false)
    private BigDecimal costoBaseDelivery;

    @Column(nullable = false)
    private BigDecimal costoPorKilometro;

    @Column(nullable = false)
    private BigDecimal montoMinimoPedido;

    @Column(nullable = false)
    private BigDecimal porcentajeGananciasRepartidor;

    @Column(nullable = false)
    private Boolean modoMantenimiento;

    @Column(length = 500)
    private String mensajeMantenimiento;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_modificacion_id")
    private Usuario usuarioModificacion;

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.fechaActualizacion = LocalDateTime.now();

        if (this.porcentajeGananciasRestaurante == null) {
            this.porcentajeGananciasRestaurante = BigDecimal.valueOf(15.0); // 15%
        }
        if (this.radioMaximoEntrega == null) {
            this.radioMaximoEntrega = BigDecimal.valueOf(10.0); // 10 km
        }
        if (this.tiempoMaximoEntrega == null) {
            this.tiempoMaximoEntrega = 45; // 45 minutos
        }
        if (this.costoBaseDelivery == null) {
            this.costoBaseDelivery = BigDecimal.valueOf(2000.0); // $2000
        }
        if (this.costoPorKilometro == null) {
            this.costoPorKilometro = BigDecimal.valueOf(500.0); // $500/km
        }
        if (this.montoMinimoPedido == null) {
            this.montoMinimoPedido = BigDecimal.valueOf(5000.0); // $5000
        }
        if (this.porcentajeGananciasRepartidor == null) {
            this.porcentajeGananciasRepartidor = BigDecimal.valueOf(80.0); // 80%
        }
        if (this.modoMantenimiento == null) {
            this.modoMantenimiento = false;
        }
        if (this.fechaActualizacion == null) {
            this.fechaActualizacion = LocalDateTime.now();
        }
    }

    /**
     * Calcula el costo de delivery basado en la distancia
     *
     * @param distanciaKm Distancia en kilómetros
     * @return Costo total de delivery
     */
    public BigDecimal calcularCostoDelivery(BigDecimal distanciaKm) {
        if (distanciaKm == null || distanciaKm.compareTo(BigDecimal.ZERO) <= 0) {
            return this.costoBaseDelivery;
        }

        BigDecimal costoDistancia = this.costoPorKilometro.multiply(distanciaKm);
        return this.costoBaseDelivery.add(costoDistancia);
    }

    /**
     * Calcula la ganancia del repartidor para un delivery
     *
     * @param costoDelivery Costo total del delivery
     * @return Ganancia del repartidor
     */
    public BigDecimal calcularGananciaRepartidor(BigDecimal costoDelivery) {
        return costoDelivery.multiply(this.porcentajeGananciasRepartidor)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calcula la comisión de la plataforma sobre el total del pedido
     *
     * @param totalPedido Total del pedido
     * @return Comisión de la plataforma
     */
    public BigDecimal calcularComisionPlataforma(BigDecimal totalPedido) {
        BigDecimal comisionPlataforma = new BigDecimal("100.00").subtract(porcentajeGananciasRestaurante);
        return totalPedido.multiply(comisionPlataforma)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }
}