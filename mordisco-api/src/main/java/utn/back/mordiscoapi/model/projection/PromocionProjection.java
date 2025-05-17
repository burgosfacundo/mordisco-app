package utn.back.mordiscoapi.model.projection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Proyección de la entidad Promoción.
 * Esta interfaz define los métodos para acceder a los atributos de la entidad Promoción.
 */
public interface PromocionProjection {
    @Schema(description = "ID de la promoción", example = "1") // Anotación para documentar un campo de ejemplo en Swagger
    Long getId();
    @Schema(description = "Nombre de la promoción", example = "Descuento del 20%")
    String getDescripcion();
    @Schema(description = "ID del restaurante al que pertenece la promoción", example = "0.20")
    Double getDescuento();
    @Schema(description = "Fecha de inicio de la promoción", example = "2025-05-10")
    LocalDate getFechaInicio();
    @Schema(description = "Fecha de fin de la promoción", example = "2025-05-10")
    LocalDate getFechaFin();

}
