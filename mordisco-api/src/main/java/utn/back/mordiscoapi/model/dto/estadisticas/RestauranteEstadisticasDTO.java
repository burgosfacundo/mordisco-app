package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;
import java.util.List;

public record RestauranteEstadisticasDTO(
        BigDecimal ingresosTotales,
        List<IngresosPorPeriodoDTO> ingresosPorPeriodo,
        List<ProductoMasVendidoDTO> productosMasVendidos
) {
}
