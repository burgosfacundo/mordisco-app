package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.repartidor.RepartidorEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.repartidor.RepartidorResponseDTO;

import java.util.List;

public interface IRepartidorService {
    List<RepartidorResponseDTO> findDisponiblesCercanos(
            Double latitud, Double longitud, Double radioKm);

    RepartidorEstadisticasDTO getEstadisticas(Long repartidorId)
            throws NotFoundException;
}
