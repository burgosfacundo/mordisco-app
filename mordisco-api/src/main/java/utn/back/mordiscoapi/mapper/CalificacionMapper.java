package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionPedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionPedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionRepartidorRequestDTO;
import utn.back.mordiscoapi.model.dto.calificaciones.CalificacionRepartidorResponseDTO;
import utn.back.mordiscoapi.model.entity.CalificacionPedido;
import utn.back.mordiscoapi.model.entity.CalificacionRepartidor;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Usuario;

@UtilityClass
public class CalificacionMapper {

    public static CalificacionPedido toEntity(CalificacionPedidoRequestDTO dto, Pedido pedido, Usuario cliente) {
        return CalificacionPedido.builder()
                .puntajeComida(dto.puntajeComida())
                .puntajeTiempo(dto.puntajeTiempo())
                .puntajePackaging(dto.puntajePackaging())
                .comentario(dto.comentario())
                .pedido(pedido)
                .usuario(cliente)
                .build();
    }

    public static CalificacionRepartidor toEntity(CalificacionRepartidorRequestDTO dto, Pedido pedido, Usuario cliente) {
        return CalificacionRepartidor.builder()
                .puntajeAtencion(dto.puntajeAtencion())
                .puntajeComunicacion(dto.puntajeComunicacion())
                .puntajeProfesionalismo(dto.puntajeProfesionalismo())
                .comentario(dto.comentario())
                .pedido(pedido)
                .repartidor(pedido.getRepartidor())
                .usuario(cliente)
                .build();
    }

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