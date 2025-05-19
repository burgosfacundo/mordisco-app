package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Usuario {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY) //primary key auto_increments
    private Long id;
    @Column (nullable = false)
    private String nombre;
    @Column (nullable = false)
    private String apellido;
    @Column (nullable = false, unique = true)
    private String telefono;
    @Column (nullable = false, unique = true)
    private String email;
    @Column (nullable = false)
    private String password;
    //Relacion a Direcciones
    @ManyToOne
    @JoinColumn(name = "rol_id",nullable = false)
    private Rol rol;
}
