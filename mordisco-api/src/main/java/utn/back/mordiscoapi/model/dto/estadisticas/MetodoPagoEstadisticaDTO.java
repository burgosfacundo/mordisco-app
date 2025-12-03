package utn.back.mordiscoapi.model.dto.estadisticas;

public record MetodoPagoEstadisticaDTO(
        String metodoPago,
        Integer cantidad,
        Double porcentaje
) {
}
