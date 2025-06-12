package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "imagenes",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_imagen_url", columnNames = {"url"})
        })
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT" )
    private String url;

    @Column (nullable = false,length = 50)
    private String nombre;
}
