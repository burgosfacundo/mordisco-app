package utn.back.mordiscoapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.repository.CalificacionRestauranteRepository;
import utn.back.mordiscoapi.utils.AuthUtils;

@Component("calificacionSecurity")
@RequiredArgsConstructor
public class CalificacionSecurity {

    private final CalificacionRestauranteRepository repo;
    private final AuthUtils authUtils;

    /**
     * Verifica si el usuario autenticado es el autor de la calificación.
     *
     * @param calificacionId ID de la calificación
     * @return true si el usuario es el autor
     */
    public boolean esAutorDeCalificacion(Long calificacionId) {
        return authUtils.getUsuarioAutenticado()
                .flatMap(user ->
                        repo.findById(calificacionId)
                                .map(calificacion -> calificacion.getUsuario().getId().equals(user.getId())))
                .orElse(false);
    }

    public boolean puedeAccederADtoConCalificacion(CalificacionRestauranteDTO dto) {
        return dto != null && dto.calificacionDTO() != null &&
                authUtils.getUsuarioAutenticado()
                        .map(user -> user.getId().equals(dto.calificacionDTO().idUsuario()))
                        .orElse(false);
    }

}


