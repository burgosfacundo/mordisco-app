package utn.back.mordiscoapi.model.dto.calificacionRestaurante;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CalificacionRestauranteDTO(
        @NotNull(message = "El restaurante id es obligatorio")
        Long restauranteId,
        @NotNull(message = "La calificación es obligatoria")
        @Valid
        CalificacionDTO calificacionDTO
) {
}
