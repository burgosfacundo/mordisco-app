package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones_productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalificacionProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //RELACIONES
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private Integer puntaje;

    @Column(nullable = false)
    private String comentario;

    @Column (nullable = false)
    private LocalDateTime fecha;
    //RELACIONES



}
