package utn.back.mordiscoapi.model.dto.ganancia;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TotalesGananciaDTO(
        @Schema(description = "Total acumulado de ganancias", example = "15000.00")
        BigDecimal totalAcumulado,

        @Schema(description = "Total del mes actual", example = "3500.00")
        BigDecimal totalMes,

        @Schema(description = "Total de la semana actual", example = "800.00")
        BigDecimal totalSemana,

        @Schema(description = "Promedio por entrega", example = "450.00")
        BigDecimal promedioEntrega,

        @Schema(description = "Cantidad total de entregas", example = "35")
        Long cantidadEntregas
) {
}
