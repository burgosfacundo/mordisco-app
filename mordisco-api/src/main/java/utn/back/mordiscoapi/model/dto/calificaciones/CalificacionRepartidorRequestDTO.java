package utn.back.mordiscoapi.model.dto.calificaciones;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalificacionRepartidorRequestDTO(
        @NotNull(message = "El ID del pedido es obligatorio")
        Long pedidoId,

        @NotNull(message = "El puntaje de atención es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajeAtencion,

        @NotNull(message = "El puntaje de comunicación es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajeComunicacion,

        @NotNull(message = "El puntaje de profesionalismo es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajeProfesionalismo,

        @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
        String comentario
) {}