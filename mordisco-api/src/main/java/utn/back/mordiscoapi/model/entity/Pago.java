package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import utn.back.mordiscoapi.enums.EstadoPago;
import utn.back.mordiscoapi.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Column(name = "mercadopago_payment_id", length = 100)
    private String mercadoPagoPaymentId;

    @Column(name = "mercadopago_preference_id", length = 100)
    private String mercadoPagoPreferenceId;

    @Column(name = "mercadopago_status", length = 50)
    private String mercadoPagoStatus;

    @Column(name = "mercadopago_status_detail", length = 100)
    private String mercadoPagoStatusDetail;

    @Column(name = "mercadopago_payment_type", length = 50)
    private String mercadoPagoPaymentType;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
