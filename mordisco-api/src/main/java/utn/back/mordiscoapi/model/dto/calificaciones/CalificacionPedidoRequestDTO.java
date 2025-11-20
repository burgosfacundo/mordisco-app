package utn.back.mordiscoapi.model.dto.calificaciones;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalificacionPedidoRequestDTO(
        @NotNull(message = "El ID del pedido es obligatorio")
        Long pedidoId,

        @NotNull(message = "El puntaje de comida es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajeComida,

        @NotNull(message = "El puntaje de tiempo es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajeTiempo,

        @NotNull(message = "El puntaje de packaging es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntajePackaging,

        @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
        String comentario
) {}
