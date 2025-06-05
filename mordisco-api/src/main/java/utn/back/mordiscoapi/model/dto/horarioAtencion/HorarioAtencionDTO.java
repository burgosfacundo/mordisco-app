package utn.back.mordiscoapi.model.dto.horarioAtencion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioAtencionDTO(
                                @Positive(message = "El ID debe ser positivo")
                                @Schema(description = "ID del horario de atención", example = "1")
                                Long id,
                                @NotNull(message = "El dia de la semana debe ser obligatorio")
                                @Schema(description = "Día de la semana para el horario de atención", example = "MONDAY")
                                DayOfWeek dia,
                                @NotNull(message = "El horario de apertura debe ser obligatorio")
                                @Schema(description = "Hora de apertura del local", example = "09:00")
                                LocalTime horaApertura,
                                @NotNull(message = "El horario de cierre debe ser obligatorio")
                                @Schema(description = "Hora de cierre del local", example = "21:00")
                                LocalTime horaCierre) {
}
