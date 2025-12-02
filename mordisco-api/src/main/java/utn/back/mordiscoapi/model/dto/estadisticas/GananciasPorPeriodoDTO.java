package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;

public record GananciasPorPeriodoDTO(
        String periodo,  // "2024-01", "2024-02", etc.
        BigDecimal ganancias
) {
}
