package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import utn.back.mordiscoapi.model.enums.AlcancePromocion;
import utn.back.mordiscoapi.model.enums.TipoDescuento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "promociones")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Promocion {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String descripcion;

    @Column (nullable = false)
    private Double descuento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento = TipoDescuento.PORCENTAJE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlcancePromocion alcance = AlcancePromocion.TODO_MENU;

    @Column (nullable = false)
    private LocalDate fechaInicio;

    @Column (nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToMany
    @JoinTable(
        name = "promocion_productos",
        joinColumns = @JoinColumn(name = "promocion_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @Builder.Default
    private List<Producto> productosAplicables = new ArrayList<>();
}

