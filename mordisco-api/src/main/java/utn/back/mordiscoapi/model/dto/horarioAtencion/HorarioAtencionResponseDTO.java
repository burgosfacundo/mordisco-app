package utn.back.mordiscoapi.model.dto.horarioAtencion;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioAtencionResponseDTO(
        @Schema(description = "ID del horario de atencion", example = "3")
        Long id,
        @Schema(description = "Día de la semana", example = "MONDAY")
        DayOfWeek dia,
        @Schema(description = "Hora de apertura", example = "08:00")
        LocalTime horaApertura,
        @Schema(description = "Hora de cierre", example = "22:00")
        LocalTime horaCierre
) {
}
