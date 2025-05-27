package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.dto.direccion.DireccionDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenRequestDTO;

public record RestauranteDTO(
                            @Size(message = "La razón social del restaurante debe tener máximo 50 caracteres", max = 50)
                            @NotBlank(message = "La razón social del restaurante es obligatoria")
                            @Schema(description = "Razón social del restaurante", example = "Mc Donald's")
                            String razonSocial,
                            @NotNull(message = "El estado del restaurante es obligatorio")
                            @Schema(description = "Si el restaurante esta activo", example = "true")
                            Boolean activo,
                            @NotNull(message = "El logo del restaurante es obligatorio")
                            ImagenRequestDTO logo,
                            @NotNull(message = "El id del usuario es obligatorio")
                            @Positive(message = "El id del usuario debe ser positivo")
                            @Schema(description = "Id del usuario dueño", example = "7")
                            Long idUsuario,
                            @NotNull(message = "La dirección del restaurante es obligatoria")
                            DireccionDTO direccion
                            ){
}
