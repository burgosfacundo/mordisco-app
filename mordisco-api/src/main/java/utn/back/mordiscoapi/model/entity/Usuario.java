package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import utn.back.mordiscoapi.security.jwt.model.entity.RefreshToken;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_usuario_telefono", columnNames = "telefono"),
                @UniqueConstraint(name = "UK_usuario_email", columnNames = "email"),
                @UniqueConstraint(name = "UK_usuario_cuil", columnNames = "cuil") // 🆕
        })
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Usuario implements UserDetails {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
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

    @Column
    private Double latitudActual;

    @Column
    private Double longitudActual;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Direccion> direcciones;

    @ManyToOne
    @JoinColumn(name = "rol_id",nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Restaurante restaurante;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

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

    public void addDireccion(Direccion d) {
        direcciones.add(d);
        d.setUsuario(this);
    }

    public void removeDireccion(Direccion d) {
        direcciones.remove(d);
        d.setUsuario(null);
    }

    public boolean isRepartidor() {
        return this.rol != null && "ROLE_REPARTIDOR".equals(this.rol.getNombre());
    }
}