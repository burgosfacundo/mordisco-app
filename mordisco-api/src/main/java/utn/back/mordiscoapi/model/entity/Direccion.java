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

    @Column(nullable = false, length = 50)
    private String calle;

    @Column(nullable = false,length = 5)
    private String numero;

    @Column(length = 20)
    private String piso;

    @Column(length = 20)
    private String depto;

    @Column(nullable = false, length = 10)
    private String codigoPostal;

    private String referencias;

    private String alias;

    @Column(nullable = false, length = 50)
    private Double latitud;

    @Column(nullable = false, length = 50)
    private Double longitud;

    @Column(nullable = false, length = 50)
    private String ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    public String toSnapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append(calle).append(" ").append(numero);

        if (piso != null && !piso.isEmpty()) {
            sb.append(", Piso ").append(piso);
            if (depto != null && !depto.isEmpty()) {
                sb.append(" ").append(depto);
            }
        }

        sb.append(", ").append(ciudad).append(" (").append(codigoPostal).append(")");

        if (referencias != null && !referencias.isEmpty()) {
            sb.append(" - ").append(referencias);
        }

        return sb.toString();
    }
}
