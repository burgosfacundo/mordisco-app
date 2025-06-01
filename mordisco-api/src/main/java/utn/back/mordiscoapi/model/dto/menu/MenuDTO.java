package utn.back.mordiscoapi.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;

import java.util.List;

public record MenuDTO(
        @Schema(description = "Id del menu", example = "5")
        @Positive(message = "El id del menu debe ser positivo")
        Long id,
        @NotBlank(message = "El nombre del menu no debe ser nulo")
        @Size(message = "El nombre del menu debe tener como maximo 50 caracteres", max = 50)
        @Schema(description = "Nombre del menu", example = "Ricoricor")
        String nombre,
        @NotNull(message = "La lista de productos no debe ser nula")
        List<ProductoRequestDTO> productos
) {
}
