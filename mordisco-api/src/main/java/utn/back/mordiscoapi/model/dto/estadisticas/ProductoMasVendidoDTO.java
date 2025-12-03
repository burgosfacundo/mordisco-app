package utn.back.mordiscoapi.model.dto.estadisticas;

import java.math.BigDecimal;

public record ProductoMasVendidoDTO(
        Long productoId,
        String nombre,
        Integer cantidadVendida,
        BigDecimal ingresoGenerado
) {
}
