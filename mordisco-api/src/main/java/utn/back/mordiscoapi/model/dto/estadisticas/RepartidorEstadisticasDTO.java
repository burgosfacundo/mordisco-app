package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;
import java.util.List;

public record RepartidorEstadisticasDTO(
        BigDecimal gananciasTotales,
        List<GananciasPorPeriodoDTO> gananciasPorPeriodo,
        Double tiempoPromedioEntrega,  // en minutos
        List<PedidosPorPeriodoDTO> pedidosPorDia,
        List<PedidosPorPeriodoDTO> pedidosPorSemana,
        List<PedidosPorPeriodoDTO> pedidosPorMes
) {
}
