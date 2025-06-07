package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.repository.CalificacionRestauranteRepository;

@Component("calificacionSecurity")
@RequiredArgsConstructor
public class CalificacionSecurity {

    private final CalificacionRestauranteRepository repo;

    /**
     * Verifica si el usuario autenticado puede eliminar una calificación.
     * Un usuario puede eliminar su propia calificación o si es un administrador.
     *
     * @param calificacionId ID de la calificación a eliminar
     * @return true si el usuario puede eliminar la calificación, false en caso contrario
     */
    public boolean puedeEliminar(Long calificacionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        Object principal = auth.getPrincipal();
        if (!(principal instanceof Usuario user)) return false;

        // Verifica si es ADMIN
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si es el dueño de la calificación o admin, puede eliminar
        return esAdmin || repo.findById(calificacionId)
                .map(c -> c.getUsuario().getId().equals(user.getId()))
                .orElse(false);
    }
}

