package utn.back.mordiscoapi.model.dto.calificaciones;

import java.time.LocalDateTime;

public record CalificacionRepartidorResponseDTO(
        Long id,
        Long pedidoId,
        Long repartidorId,
        String repartidorNombre,
        Integer puntajeAtencion,
        Integer puntajeComunicacion,
        Integer puntajeProfesionalismo,
        Double puntajePromedio,
        String comentario,
        LocalDateTime fechaHora,
        String clienteNombre
) {}