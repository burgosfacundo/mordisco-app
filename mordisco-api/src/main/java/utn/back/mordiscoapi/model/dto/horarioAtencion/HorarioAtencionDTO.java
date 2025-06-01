package utn.back.mordiscoapi.model.dto.horarioAtencion;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioAtencionDTO(
                                @NotNull(message = "El dia de la semana debe ser obligatorio")
                                DayOfWeek dia,
                                @NotNull(message = "El horario de apertura debe ser obligatorio")
                                LocalTime horaApertura,
                                @NotNull(message = "El horario de cierre debe ser obligatorio")
                                LocalTime horaCierre){
}
