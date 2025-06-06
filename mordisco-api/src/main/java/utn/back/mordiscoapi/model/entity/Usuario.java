package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_usuario_telefono", columnNames = "telefono"),
                @UniqueConstraint(name = "UK_usuario_email", columnNames = "email")
})
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Usuario implements UserDetails {
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private List<Direccion> direcciones;

    @ManyToOne
    @JoinColumn(name = "rol_id",nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Restaurante restaurante;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalificacionRestaurante> calificacionesRestaurante;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var grantedAuthority = new SimpleGrantedAuthority(rol.getNombre());
        return Collections.singletonList(grantedAuthority);
    }

    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public String getPassword(){ return this.password; }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
