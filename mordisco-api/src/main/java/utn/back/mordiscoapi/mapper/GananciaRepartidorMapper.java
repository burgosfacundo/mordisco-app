package utn.back.mordiscoapi.mapper;

import utn.back.mordiscoapi.model.dto.ganancia.GananciaRepartidorResponseDTO;
import utn.back.mordiscoapi.model.entity.GananciaRepartidor;

public class GananciaRepartidorMapper {

    public static GananciaRepartidorResponseDTO toDTO(GananciaRepartidor entity) {
        if (entity == null) {
            return null;
        }

        return new GananciaRepartidorResponseDTO(
                entity.getId(),
                entity.getPedido().getId(),
                "#" + entity.getPedido().getId(),
                entity.getCostoDelivery(),
                entity.getComisionPlataforma(),
                entity.getGananciaRepartidor(),
                entity.getPorcentajeAplicado(),
                entity.getFechaRegistro()
        );
    }
}
