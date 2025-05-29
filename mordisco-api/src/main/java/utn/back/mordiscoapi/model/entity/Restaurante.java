package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "restaurantes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String razonSocial; //si

    @Column(nullable = false)
    private Boolean activo; //si

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imagen_id", referencedColumnName = "id", unique = true)
    private Imagen imagen; //si

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Promocion> promociones;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Set<HorarioAtencion> horariosAtencion; //si

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CalificacionRestaurante> calificaciones;

    @OneToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario; //id

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion; //si
}
