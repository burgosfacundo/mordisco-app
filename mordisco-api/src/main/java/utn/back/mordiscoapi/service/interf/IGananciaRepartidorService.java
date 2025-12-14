package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.ganancia.GananciaRepartidorResponseDTO;
import utn.back.mordiscoapi.model.dto.ganancia.TotalesGananciaDTO;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.time.LocalDateTime;

public interface IGananciaRepartidorService {

    /**
     * Registra la ganancia de un repartidor al completar un pedido
     */
    void registrarGanancia(Pedido pedido) throws NotFoundException;

    /**
     * Obtiene las ganancias de un repartidor
     */
    Page<GananciaRepartidorResponseDTO> getGanancias(
            Long repartidorId,
            int page,
            int size
    ) throws NotFoundException;

    /**
     * Obtiene ganancias en un rango de fechas
     */
    Page<GananciaRepartidorResponseDTO> getGananciasEnRango(
            Long repartidorId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            int page,
            int size
    ) throws NotFoundException;

    /**
     * Obtiene los totales de ganancias de un repartidor
     */
    TotalesGananciaDTO getTotales(Long repartidorId) throws NotFoundException;
}
