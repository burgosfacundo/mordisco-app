package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.utils.AuthUtils;

@Component("restauranteSecurity")
@RequiredArgsConstructor
public class RestauranteSecurity {
    private final AuthUtils authUtils;
    private final RestauranteRepository restauranteRepository;

    /**
     * Verifica si el usuario autenticado está accediendo a su propio restaurante.
     *
     * @param id ID del restaurante a consultar
     * @return true si el ID coincide con el del restaurante del usuario autenticado
     */
    public boolean puedeAccederAPropioRestaurante(Long id) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(user ->
                        restauranteRepository.findById(id)
                                .map(restaurante -> restaurante.getUsuario().getId().equals(user.getId())))
                .orElse(false);
    }
}
