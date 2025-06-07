package utn.back.mordiscoapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.model.entity.Usuario;

@Component("usuarioSecurity")
public class UsuarioSecurity {

    /**
     * Verifica si el usuario autenticado puede acceder a los datos del usuario con el ID proporcionado.
     * Un usuario puede acceder a sus propios datos o si es un administrador.
     *
     * @param id ID del usuario al que se desea acceder
     * @return true si el usuario puede acceder, false en caso contrario
     */
    public boolean puedeAccederAUsuario(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario user) {
            return user.getId().equals(id) || user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }

        return false;
    }

    /**
     * Verifica si el usuario autenticado es un administrador.
     *
     * @return true si el usuario es un administrador, false en caso contrario
     */
    public boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

