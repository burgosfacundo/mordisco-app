package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionPedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionRepartidorResponseDTO;
import utn.back.mordiscoapi.model.entity.CalificacionPedido;
import utn.back.mordiscoapi.model.entity.CalificacionRepartidor;

@UtilityClass
public class CalificacionMapper {

    /**
     * Convierte CalificacionPedido entity a DTO
     */
    public static CalificacionPedidoResponseDTO toDTO(CalificacionPedido entity) {
        if (entity == null) return null;

        String clienteNombre = entity.getUsuario().getNombre() + " " +
                entity.getUsuario().getApellido();

        return new CalificacionPedidoResponseDTO(
                entity.getId(),
                entity.getPedido().getId(),
                entity.getPuntajeComida(),
                entity.getPuntajeTiempo(),
                entity.getPuntajePackaging(),
                entity.getPuntajePromedio(),
                entity.getComentario(),
                entity.getFechaHora(),
                clienteNombre
        );
    }

    /**
     * Convierte CalificacionRepartidor entity a DTO
     */
    public static CalificacionRepartidorResponseDTO toDTO(CalificacionRepartidor entity) {
        if (entity == null) return null;

        String clienteNombre = entity.getUsuario().getNombre() + " " +
                entity.getUsuario().getApellido();
        String repartidorNombre = entity.getRepartidor().getNombre() + " " +
                entity.getRepartidor().getApellido();

        return new CalificacionRepartidorResponseDTO(
                entity.getId(),
                entity.getPedido().getId(),
                entity.getRepartidor().getId(),
                repartidorNombre,
                entity.getPuntajeAtencion(),
                entity.getPuntajeComunicacion(),
                entity.getPuntajeProfesionalismo(),
                entity.getPuntajePromedio(),
                entity.getComentario(),
                entity.getFechaHora(),
                clienteNombre
        );
    }
}