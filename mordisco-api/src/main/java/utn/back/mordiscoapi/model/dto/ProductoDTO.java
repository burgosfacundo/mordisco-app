package utn.back.mordiscoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoDTO(
        @Size(message = "El nombre del producto debe tener como maximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre del producto es obligatorio")
        // Anotación para documentar un campo de ejemplo en Swagger
        @Schema(description = "Nombre del producto", example = "flan mixto")
        String nombre,
        @Size(message = "La descripción del producto debe tener máximo 255 caracteres", max = 255)
        @NotNull(message = "La descripción del producto es obligatoria")
        // Anotación para documentar un campo de ejemplo en Swagger
        @Schema(description = "Descripción del producto", example = "Flan casero con dulce de leche y crema")
        String descripcion,
        @Size(message = "El precio no puede ser mayor a 100.000,00", max = 100000)
        @NotNull (message = "El precio del producto es obligatorio")
        @Schema(description = "Precio del producto", example = "20000")
        Double precio,
        @Size(message = "La disponibilidad se marca con false (no disponible), true(disponible)", max = 5)
        @NotNull(message = "La disponibilidad es obligatoria")
        @Schema(description = "El producto esta disponible", example = "true")
        Boolean disponible

) {

}
