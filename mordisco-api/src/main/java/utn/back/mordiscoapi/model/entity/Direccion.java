package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "direcciones")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Direccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String calle;
    @Column(nullable = false)
    private String numero;
    private String piso;
    private String depto;
    @Column(nullable = false)
    private String codigoPostal;
    private String referencias;
    @Column(nullable = false)
    private Double latitud;
    @Column(nullable = false)
    private Double longitud;
    @Column(nullable = false)
    private String ciudad; ///TIPO CIUDAD PERO NO EXISTE AUN
}
