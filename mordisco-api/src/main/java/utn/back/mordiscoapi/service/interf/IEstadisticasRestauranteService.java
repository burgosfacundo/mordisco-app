package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.estadisticas.RestauranteEstadisticasDTO;

public interface IEstadisticasRestauranteService {
    /**
     * Obtiene las estadísticas de un restaurante
     * @param restauranteId ID del restaurante
     * @return Estadísticas del restaurante
     */
    RestauranteEstadisticasDTO getEstadisticas(Long restauranteId);

    /**
     * Obtiene las estadísticas del restaurante asociado a un usuario
     * @param usuarioId ID del usuario
     * @return Estadísticas del restaurante
     */
    RestauranteEstadisticasDTO getEstadisticasByUsuarioId(Long usuarioId) throws NotFoundException;
}
