package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Pedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrega tipoEntrega;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false)
    private BigDecimal total;

    @Column
    private BigDecimal subtotalProductos;

    @Column
    private BigDecimal costoDelivery;

    @Column
    private BigDecimal distanciaKm;

    @Builder.Default
    @Column(nullable = false)
    private Boolean bajaLogica = false;

    @Column(length = 500)
    private String motivoBaja;

    @Column
    private java.time.LocalDateTime fechaBaja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EstadoPedido estadoAntesDeCancelado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;

    @Column
    private LocalDateTime fechaAceptacionRepartidor;

    @Column
    private LocalDateTime fechaEntrega;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Direccion direccionEntrega;

    @Column(length = 500)
    private String direccionSnapshot;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private CalificacionPedido calificacionPedido;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private CalificacionRepartidor calificacionRepartidor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPedido> items;

    public boolean necesitaRepartidor() {
        return TipoEntrega.DELIVERY.equals(this.tipoEntrega) && this.repartidor == null;
    }

    public boolean puedeSerAceptadoPorRepartidor() {
        return EstadoPedido.LISTO_PARA_ENTREGAR.equals(this.estado) && this.repartidor == null;
    }

    public boolean puedeSerCalificado() {
        return EstadoPedido.COMPLETADO.equals(this.estado)
                && this.calificacionPedido == null;
    }

    public boolean repartidorPuedeSerCalificado() {
        return EstadoPedido.COMPLETADO.equals(this.estado)
                && this.repartidor != null
                && this.calificacionRepartidor == null;
    }
}