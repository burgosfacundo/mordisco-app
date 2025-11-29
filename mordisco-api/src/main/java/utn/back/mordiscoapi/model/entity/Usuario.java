package utn.back.mordiscoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
                @UniqueConstraint(name = "UK_usuario_email", columnNames = "email")
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

    @Column(nullable = false)
    private Boolean bajaLogica = false;

    @Column(length = 500)
    private String motivoBaja;

    @Column
    private java.time.LocalDateTime fechaBaja;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Direccion> direcciones;

    @ManyToOne
    @JoinColumn(name = "rol_id",nullable = false)
    private Rol rol;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Restaurante restaurante;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    /**
     * Calificaciones de pedidos realizadas por este usuario (como cliente)
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CalificacionPedido> calificacionesPedidoRealizadas = new ArrayList<>();

    /**
     * Calificaciones de repartidores realizadas por este usuario (como cliente)
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CalificacionRepartidor> calificacionesRepartidorRealizadas = new ArrayList<>();

    /**
     * Calificaciones recibidas por este usuario cuando actúa como repartidor
     * Solo aplica si el usuario tiene rol REPARTIDOR
     */
    @OneToMany(mappedBy = "repartidor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CalificacionRepartidor> calificacionesRecibidas = new ArrayList<>();

    // ========== MÉTODOS DE CALIFICACIONES ==========

    /**
     * Calcula el promedio de calificaciones recibidas como repartidor
     * @return Promedio de 1-5, o 0.0 si no tiene calificaciones
     */
    public Double getPromedioCalificacionRepartidor() {
        if (!isRepartidor() || calificacionesRecibidas == null || calificacionesRecibidas.isEmpty()) {
            return 0.0;
        }

        return calificacionesRecibidas.stream()
                .mapToDouble(CalificacionRepartidor::getPuntajePromedio)
                .average()
                .orElse(0.0);
    }

    /**
     * Cuenta cuántas calificaciones tiene como repartidor
     */
    public Long getTotalCalificacionesRepartidor() {
        if (!isRepartidor() || calificacionesRecibidas == null) {
            return 0L;
        }
        return (long) calificacionesRecibidas.size();
    }

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
        return !bajaLogica;
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