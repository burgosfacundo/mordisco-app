package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;

public record RepartidorMasActivoDTO(
        Long repartidorId,
        String nombre,
        Integer entregasRealizadas,
        BigDecimal gananciaGenerada
) {
}
