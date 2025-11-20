package utn.back.mordiscoapi.model.dto.calificaciones;

import java.time.LocalDateTime;

public record CalificacionPedidoResponseDTO(
        Long id,
        Long pedidoId,
        Integer puntajeComida,
        Integer puntajeTiempo,
        Integer puntajePackaging,
        Double puntajePromedio,
        String comentario,
        LocalDateTime fechaHora,
        String clienteNombre
) {}