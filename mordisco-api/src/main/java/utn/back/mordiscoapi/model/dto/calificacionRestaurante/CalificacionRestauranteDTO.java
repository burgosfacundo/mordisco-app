package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CalificacionRestauranteDTO(
        @Positive
        @NotNull(message = "El puntaje es obligatorio")
        int puntaje,

        @Size(message = "El comentario debe tener máximo 255 caracteres", max = 255)
        @NotNull(message = "El comentario es obligatorio")
        String comentario,

        @NotNull(message = "La fecha/hora es obligatoria")
        LocalDateTime fechaHora,

        @NotNull(message = "El restauranteid es obligatorio")
        Long restauranteId,

        @NotNull(message = "El usuarioId es obligatorio")
        Long usuarioId
) {
}
