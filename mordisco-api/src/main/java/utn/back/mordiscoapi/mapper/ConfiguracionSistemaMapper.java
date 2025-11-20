package utn.back.mordiscoapi.mapper;

import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaRequestDTO;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaResponseDTO;
import utn.back.mordiscoapi.model.entity.ConfiguracionSistema;

public class ConfiguracionSistemaMapper {

    public static ConfiguracionSistemaResponseDTO toDTO(ConfiguracionSistema entity) {
        if (entity == null) return null;

        return new ConfiguracionSistemaResponseDTO(
                entity.getId(),
                entity.getComisionPlataforma(),
                entity.getRadioMaximoEntrega(),
                entity.getTiempoMaximoEntrega(),
                entity.getCostoBaseDelivery(),
                entity.getCostoPorKilometro(),
                entity.getMontoMinimoPedido(),
                entity.getPorcentajeGananciasRepartidor(),
                entity.getModoMantenimiento(),
                entity.getMensajeMantenimiento(),
                entity.getFechaActualizacion(),
                entity.getUsuarioModificacion() != null
                        ? entity.getUsuarioModificacion().getEmail()
                        : null
        );
    }

    public static void applyUpdate(ConfiguracionSistemaRequestDTO dto, ConfiguracionSistema entity) {
        if (dto == null || entity == null) return;

        entity.setComisionPlataforma(dto.comisionPlataforma());
        entity.setRadioMaximoEntrega(dto.radioMaximoEntrega());
        entity.setTiempoMaximoEntrega(dto.tiempoMaximoEntrega());
        entity.setCostoBaseDelivery(dto.costoBaseDelivery());
        entity.setCostoPorKilometro(dto.costoPorKilometro());
        entity.setMontoMinimoPedido(dto.montoMinimoPedido());
        entity.setPorcentajeGananciasRepartidor(dto.porcentajeGananciasRepartidor());
        entity.setModoMantenimiento(dto.modoMantenimiento());
        entity.setMensajeMantenimiento(dto.mensajeMantenimiento());
    }
}