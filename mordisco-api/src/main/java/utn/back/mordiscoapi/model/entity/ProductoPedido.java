package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductoPedidos")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ProductoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    @ManyToOne
    @JoinColumn(name = "producto_id",nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name="pedido_id",nullable = false)
    private Pedido pedido;
}
