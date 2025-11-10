package utn.back.mordiscoapi.model.dto.notificacion;

import utn.back.mordiscoapi.enums.TipoNotificacion;

public record NotificacionDTO(
        TipoNotificacion tipo,
        String mensaje,
        Long pedidoId,
        String estado
) {
}
