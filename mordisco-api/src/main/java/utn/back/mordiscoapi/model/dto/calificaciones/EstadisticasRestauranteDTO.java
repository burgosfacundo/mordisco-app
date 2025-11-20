package utn.back.mordiscoapi.model.dto.calificaciones;

public record EstadisticasRestauranteDTO(
        Long restauranteId,
        String restauranteNombre,
        Double promedioComida,
        Double promedioTiempo,
        Double promedioPackaging,
        Double promedioGeneral,
        Long totalCalificaciones
) {}