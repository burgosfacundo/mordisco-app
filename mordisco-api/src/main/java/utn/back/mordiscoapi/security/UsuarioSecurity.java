package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;

@Component("usuarioSecurity")
@RequiredArgsConstructor
public class UsuarioSecurity {

    private final AuthUtils authUtils;

    /**
     * Verifica si el usuario autenticado está accediendo a su propia información.
     *
     * @param id ID del usuario a consultar
     * @return true si el ID coincide con el del usuario autenticado
     */
    public boolean puedeAccederAUsuario(Long id) {
        return authUtils.getUsuarioAutenticado()
                .map(user -> user.getId().equals(id))
                .orElse(false);
    }
}



