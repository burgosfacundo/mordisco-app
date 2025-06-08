package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.utils.AuthUtils;

@Component("promocionSecurity")
@RequiredArgsConstructor
public class PromocionSecurity {
    private final AuthUtils authUtils;
    private final PromocionRepository promocionRepository;

    /**
     * Verifica si el usuario autenticado puede acceder a las promociones de su propio restaurante.
     * @param promocionId ID de la promoción a verificar.
     * @return true si el usuario puede acceder, false en caso contrario.
     */
    public boolean puedeAccederAPromocion(Long promocionId) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(usuario -> promocionRepository.findById(promocionId)
                        .map(promocion -> promocion.getRestaurante().getUsuario().getId()
                                .equals(usuario.getId())))
                .orElse(false);
    }
}
