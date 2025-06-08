package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenUpdateDTO;

public record RestauranteUpdateDTO(
        @Positive(message = "El id del restaurante debe ser positivo")
        @NotNull(message = "El id del restaurante es obligatorio")
        @Schema(description = "Id del restaurante", example = "1")
        Long id,
        @Size(message = "La razón social del restaurante debe tener máximo 50 caracteres", max = 50)
        @NotBlank(message = "La razón social del restaurante es obligatoria")
        @Schema(description = "Razón social del restaurante", example = "Mc Donald's")
        String razonSocial,
        @NotNull(message = "El estado del restaurante es obligatorio")
        @Schema(description = "Si el restaurante esta activo", example = "true")
        Boolean activo,
        @NotNull(message = "El logo del restaurante es obligatorio")
        @Schema(description = "Logo del restaurante")
        @Valid
        ImagenUpdateDTO logo,
        @NotNull(message = "La dirección del restaurante es obligatoria")
        @Schema(description = "Dirección del restaurante")
        @Valid
        DireccionUpdateDTO direccion) {
}
