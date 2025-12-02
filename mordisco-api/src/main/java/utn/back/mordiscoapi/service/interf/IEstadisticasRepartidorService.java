package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.model.dto.estadisticas.RepartidorEstadisticasDTO;

public interface IEstadisticasRepartidorService {
    /**
     * Obtiene las estadísticas de un repartidor
     * @param repartidorId ID del repartidor
     * @return Estadísticas del repartidor
     */
    RepartidorEstadisticasDTO getEstadisticas(Long repartidorId);
}
