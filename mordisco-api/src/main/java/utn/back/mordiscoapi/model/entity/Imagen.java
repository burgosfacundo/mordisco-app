package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "imagenes")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String url;

    @Column (nullable = false,length = 50)
    private String nombre;
}
