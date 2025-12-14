package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "restaurantes",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_restaurante_razon_social", columnNames = "razon_social"),
                @UniqueConstraint(name = "UK_restaurante_usuario", columnNames = "usuario_id"),
                @UniqueConstraint(name = "UK_restaurante_imagen", columnNames = "imagen_id"),
                @UniqueConstraint(name = "UK_restaurante_direccion", columnNames = "direccion_id")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String razonSocial;

    @Column(nullable = false)
    private Boolean activo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imagen_id", referencedColumnName = "id", unique = true)
    private Imagen imagen;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Promocion> promociones;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<HorarioAtencion> horariosAtencion;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion;

    @OneToMany(mappedBy = "restaurante", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    /**
     * Obtiene las calificaciones del restaurante a través de sus pedidos
     * @return Lista de calificaciones recibidas
     */
    public List<CalificacionPedido> getCalificaciones() {
        if (pedidos == null || pedidos.isEmpty()) {
            return new ArrayList<>();
        }

        return pedidos.stream()
                .map(Pedido::getCalificacionPedido)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Calcula el promedio de calificaciones del restaurante
     * @return Promedio de 1-5, o 0.0 si no tiene calificaciones
     */
    public Double getPromedioCalificaciones() {
        List<CalificacionPedido> calificaciones = getCalificaciones();

        if (calificaciones.isEmpty()) {
            return 0.0;
        }

        return calificaciones.stream()
                .mapToDouble(CalificacionPedido::getPuntajePromedio)
                .average()
                .orElse(0.0);
    }

    /**
     * Cuenta el total de calificaciones recibidas
     */
    public Long getTotalCalificaciones() {
        return (long) getCalificaciones().size();
    }
}
