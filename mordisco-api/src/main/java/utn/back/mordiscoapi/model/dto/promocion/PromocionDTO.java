package utn.back.mordiscoapi.model.dto.promocion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Data Transfer Object(DTO) para la entidad Promoción
public record PromocionDTO(
        // Anotación de Spring Validation que valida tamaño para el campo descripción
        @Size(message = "La descripción de la promoción debe tener máximo 255 caracteres", max = 255)
        @NotNull(message = "La descripción de la promoción es obligatoria")
        // Anotación para documentar un campo de ejemplo en Swagger
        @Schema(description = "Descripción de la promoción", example = "Descuento del 20% en hamburguesas")
        String descripcion,
        // Anotación de Spring Validation que valida que no sea nulo para el campo descuento
        @NotNull(message = "El descuento de la promoción es obligatorio")
        // Anotación de Spring Validation que valida que el descuento sea positivo
        @Positive(message = "El descuento de la promoción no puede ser menor o igual 0")
        @Schema(description = "Descuento de la promoción", example = "0.20")
        Double descuento,
        // Anotación de Spring Validation que valida que no sea nulo para el campo fechaInicio
        @NotNull(message = "La fecha de inicio de la promoción es obligatoria")
        @Schema(description = "Fecha de inicio de la promoción", example = "2023-10-01")
        LocalDate fechaInicio,
        @NotNull(message = "La fecha de fin de la promoción es obligatoria")
        @Schema(description = "Fecha de fin de la promoción", example = "2023-10-31")
        LocalDate fechaFin) {
}
