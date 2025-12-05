package utn.back.mordiscoapi.model.dto.promocion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.enums.AlcancePromocion;
import utn.back.mordiscoapi.model.enums.TipoDescuento;

import java.time.LocalDate;
import java.util.List;

public record PromocionRequestDTO(
        @Size(message = "La descripción de la promoción debe tener máximo 255 caracteres", max = 255)
        @NotBlank(message = "La descripción de la promoción es obligatoria")
        @Schema(description = "Descripción de la promoción", example = "Descuento del 20% en hamburguesas")
        String descripcion,

        @NotNull(message = "El descuento de la promoción es obligatorio")
        @Positive(message = "El descuento de la promoción no puede ser menor o igual 0")
        @Schema(description = "Descuento de la promoción (porcentaje o monto fijo según tipo)", example = "20")
        Double descuento,

        @NotNull(message = "El tipo de descuento es obligatorio")
        @Schema(description = "Tipo de descuento: PORCENTAJE o MONTO_FIJO", example = "PORCENTAJE")
        TipoDescuento tipoDescuento,

        @NotNull(message = "El alcance de la promoción es obligatorio")
        @Schema(description = "Alcance: TODO_MENU o PRODUCTOS_ESPECIFICOS", example = "TODO_MENU")
        AlcancePromocion alcance,

        @NotNull(message = "La fecha de inicio de la promoción es obligatoria")
        @Schema(description = "Fecha de inicio de la promoción", example = "2023-10-01")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin de la promoción es obligatoria")
        @Schema(description = "Fecha de fin de la promoción", example = "2023-10-31")
        LocalDate fechaFin,

        @Schema(description = "Indica si la promoción está activa", example = "true")
        Boolean activa,

        @Schema(description = "IDs de productos específicos (solo si alcance es PRODUCTOS_ESPECIFICOS)")
        List<Long> productosIds,

        @NotNull(message = "El ID del restaurante es obligatorio")
        @Positive(message = "El ID del restaurante debe ser positivo")
        @Schema(description = "ID del restaurante al que pertenece la promoción", example = "1")
        Long restauranteId
        ) { }

