package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.model.dto.estadisticas.RestauranteEstadisticasDTO;

public interface IEstadisticasRestauranteService {
    /**
     * Obtiene las estadísticas de un restaurante
     * @param restauranteId ID del restaurante
     * @return Estadísticas del restaurante
     */
    RestauranteEstadisticasDTO getEstadisticas(Long restauranteId);
}
