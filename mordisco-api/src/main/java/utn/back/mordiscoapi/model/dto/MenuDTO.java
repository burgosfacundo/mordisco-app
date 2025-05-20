package utn.back.mordiscoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MenuDTO(
        @NotNull(message = "El nombre del menu no debe ser nulo")
        @Size(message = "El nombre del menu debe tener como maximo 50 caracteres", max = 50)
        @Schema(description = "Nombre del menu", example = "Ricoricor")
        String nombre,
        @NotNull(message = "La lista de productos no debe ser nula")
        List<ProductoDTO> productos
) {
}
