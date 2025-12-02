package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;

public record RestauranteMasActivoDTO(
        Long restauranteId,
        String nombre,
        Integer pedidosCompletados,
        BigDecimal ingresoGenerado
) {
}
