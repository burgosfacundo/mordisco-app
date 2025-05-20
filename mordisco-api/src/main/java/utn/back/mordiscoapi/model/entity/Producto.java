package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column (nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column (nullable = false)
    private Boolean disponible;
    //RELACIONES
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalificacionProducto> calificaciones;
    //RELACIONES
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imagen_id", referencedColumnName = "id", unique = true)
    private Imagen imagen;

    @OneToMany(mappedBy = "productoPedido_id", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPedido> productoPedido;
}
