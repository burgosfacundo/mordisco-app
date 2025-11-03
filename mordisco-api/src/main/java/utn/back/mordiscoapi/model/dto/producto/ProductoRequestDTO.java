package utn.back.mordiscoapi.model.dto.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import utn.back.mordiscoapi.model.dto.imagen.ImagenCreateDTO;

import java.math.BigDecimal;

public record ProductoRequestDTO(
        @Positive(message = "El id del menu debe ser positivo")
        @Schema(description = "Id del menu al que pertenece", example = "5")
        Long idMenu,
        @Size(message = "El nombre del producto debe tener como maximo 50 caracteres", max = 50)
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Schema(description = "Nombre del producto", example = "flan mixto")
        String nombre,
        @Size(message = "La descripción del producto debe tener máximo 255 caracteres", max = 255)
        @NotNull(message = "La descripción del producto es obligatoria")
        @Schema(description = "Descripción del producto", example = "Flan casero con dulce de leche y crema")
        String descripcion,
        @NotNull (message = "El precio del producto es obligatorio")
        @Positive(message = "El precio del producto no puede ser menor o igual 0")
        @Schema(description = "Precio del producto", example = "20000")
        BigDecimal precio,
        @NotNull(message = "La disponibilidad es obligatoria")
        @Schema(description = "El producto esta disponible", example = "true")
        Boolean disponible,
        @NotNull(message = "La imagen es obligatoria")
        @Valid
        ImagenCreateDTO imagen
) {

}
