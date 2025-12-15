package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "productos_pedidos")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ProductoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    // Snapshot del producto
    @Column(nullable = false, length = 50)
    private String nombreProducto;

    @Column(nullable = false)
    private String descripcionProducto;

    @Column(length = 500)
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name="pedido_id",nullable = false)
    private Pedido pedido;
}
