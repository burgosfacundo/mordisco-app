package utn.back.mordiscoapi.security.jwt.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.Optional;

@Component
public class AuthUtils {

    /**
     * Obtiene el usuario autenticado actualmente.
     * Si no hay un usuario autenticado, devuelve un Optional vacío.
     *
     * @return Optional con el usuario autenticado o vacío si no hay ninguno
     */
    public Optional<Usuario> getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        Object principal = auth.getPrincipal();
        if (principal instanceof Usuario user) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}

