package utn.back.mordiscoapi.model.dto.repartidor;

import java.math.BigDecimal;

public record RepartidorEstadisticasDTO(
        Long totalEntregas,
        BigDecimal totalGanado,
        BigDecimal promedioCalificacion,
        Long entregasHoy,
        Long entregasEstaSemana,
        Long entregasEsteMes
) {}