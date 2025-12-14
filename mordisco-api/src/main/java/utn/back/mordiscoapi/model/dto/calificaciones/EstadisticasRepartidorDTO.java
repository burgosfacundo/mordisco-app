package utn.back.mordiscoapi.model.dto.calificaciones;

public record EstadisticasRepartidorDTO(
        Long repartidorId,
        String repartidorNombre,
        Double promedioAtencion,
        Double promedioComunicacion,
        Double promedioProfesionalismo,
        Double promedioGeneral,
        Long totalCalificaciones
) {}
