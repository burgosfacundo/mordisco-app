package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Usuario cliente;
    @Column(nullable = false)
    private Usuario restaurante;
    @Column(nullable = false)
    private String direccionEntrega; ///Tipo de dato: Direccion
    @Column(nullable = false)
    private String tipoEntrega; ///Tipo de dato: TipoEntrega
    @Column(nullable = false)
    private String tipoPago; /// Tipo de dato: TipoPago
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    @Column(nullable = false)
    private String estado; ///Tipo de dato: EstadoPedido
    @Column(nullable = false)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<String> items; ///Tipo de dato: ProductoPedido*/
    @Column(nullable = false)
    private BigDecimal total;/*
    @Column(nullable = false)
    private String calificacionRestaurante; ///Tipo de dato: CalificacionRestaurante*/

}
