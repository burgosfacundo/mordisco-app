package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.repartidor.RepartidorEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.repartidor.RepartidorResponseDTO;

import java.util.List;

public interface IRepartidorService {
    List<RepartidorResponseDTO> findDisponiblesCercanos(
            Double latitud, Double longitud, Double radioKm);

    RepartidorEstadisticasDTO getEstadisticas(Long repartidorId)
            throws NotFoundException;

    Page<PedidoResponseDTO> findPedidosByRepartidor(int pageNo, int pageSize, Long id)
            throws NotFoundException;

    Page<PedidoResponseDTO> findPedidosByRepartidor_EnCamino (int pageNo, int pageSize,Long id)
            throws  NotFoundException;
}
