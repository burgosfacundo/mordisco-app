package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import utn.back.mordiscoapi.Enums.EstadoPedido;
import utn.back.mordiscoapi.Enums.TipoEntrega;
import utn.back.mordiscoapi.Enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "Pedidos")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Pedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private TipoEntrega tipoEntrega;
    @Column(nullable = false)
    private TipoPago tipoPago;
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    @Column(nullable = false)
    private EstadoPedido estado;
    @Column(nullable = false)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id",nullable = false)
    private Restaurante restaurante;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccionEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPedido> items;

/*
    @Column(nullable = false)
    private String calificacionRestaurante; ///Tipo de dato: CalificacionRestaurante*/

}
