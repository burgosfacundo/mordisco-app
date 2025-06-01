package utn.back.mordiscoapi.model.dto.horarioAtencion;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioAtencionResponseDTO(
        @Schema(description = "ID del horario de atencion", example = "3")
        Long id,
        DayOfWeek dia,
        LocalTime horaApertura,
        LocalTime horaCierre
) {
}
