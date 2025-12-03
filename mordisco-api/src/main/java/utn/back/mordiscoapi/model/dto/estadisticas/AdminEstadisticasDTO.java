package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;
import java.util.List;

public record AdminEstadisticasDTO(
        UsuariosTotalesDTO usuariosTotales,
        Integer totalPedidos,
        BigDecimal ingresosTotalesPlataforma,
        List<MetodoPagoEstadisticaDTO> metodosPagoMasUsados,
        List<RestauranteMasActivoDTO> restaurantesMasActivos,
        List<RepartidorMasActivoDTO> repartidoresMasActivos
) {
}
