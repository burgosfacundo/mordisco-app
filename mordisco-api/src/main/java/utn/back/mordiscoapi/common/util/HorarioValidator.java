package utn.back.mordiscoapi.common.util;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Validador de horarios de atención de restaurantes.
 * Verifica que no haya solapamientos ni duplicados.
 */
@UtilityClass
public class HorarioValidator {

    /**
     * Valida que un nuevo horario no se solape con los existentes
     *
     * @param dia Día de la semana del nuevo horario
     * @param horaApertura Hora de apertura del nuevo horario
     * @param horaCierre Hora de cierre del nuevo horario
     * @param horariosExistentes Lista de horarios ya registrados para el restaurante
     * @param horarioIdExcluir ID del horario a excluir en caso de actualización (null si es creación)
     * @throws IllegalArgumentException si hay conflicto
     */
    public void validarNoSolapamiento(
            DayOfWeek dia,
            LocalTime horaApertura,
            LocalTime horaCierre,
            List<HorarioAtencion> horariosExistentes,
            Long horarioIdExcluir
    ) {
        // 1. Validar que hora apertura < hora cierre
        if (!horaApertura.isBefore(horaCierre)) {
            throw new IllegalArgumentException(
                    "La hora de apertura debe ser anterior a la hora de cierre"
            );
        }

        // 2. Filtrar horarios del mismo día (excluyendo el actual si es update)
        List<HorarioAtencion> horariosMismoDia = horariosExistentes.stream()
                .filter(h -> h.getDia().equals(dia))
                .filter(h -> !h.getId().equals(horarioIdExcluir))
                .toList();

        // 3. Verificar solapamiento con cada horario existente
        for (HorarioAtencion existente : horariosMismoDia) {
            if (haySolapamiento(horaApertura, horaCierre, existente)) {
                throw new IllegalArgumentException(
                        String.format(
                                "El horario se solapa con un horario existente: %s %s - %s",
                                formatearDia(dia),
                                existente.getHoraApertura(),
                                existente.getHoraCierre()
                        )
                );
            }
        }
    }

    /**
     * Verifica si dos rangos horarios se solapan
     */
    private boolean haySolapamiento(
            LocalTime nuevaApertura,
            LocalTime nuevoCierre,
            HorarioAtencion existente
    ) {
        LocalTime existenteApertura = existente.getHoraApertura();
        LocalTime existenteCierre = existente.getHoraCierre();

        // Caso 1: El nuevo horario empieza antes y termina después del existente (lo engloba)
        if (nuevaApertura.isBefore(existenteApertura) && nuevoCierre.isAfter(existenteCierre)) {
            return true;
        }

        // Caso 2: El nuevo horario empieza durante el existente
        if ((nuevaApertura.isAfter(existenteApertura) || nuevaApertura.equals(existenteApertura))
                && nuevaApertura.isBefore(existenteCierre)) {
            return true;
        }

        // Caso 3: El nuevo horario termina durante el existente
        if (nuevoCierre.isAfter(existenteApertura)
                && (nuevoCierre.isBefore(existenteCierre) || nuevoCierre.equals(existenteCierre))) {
            return true;
        }

        // Caso 4: El existente empieza durante el nuevo (el nuevo engloba al existente)
        if (existenteApertura.isAfter(nuevaApertura) && existenteApertura.isBefore(nuevoCierre)) {
            return true;
        }

        return false;
    }

    /**
     * Formatea el día de la semana en español
     */
    private String formatearDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
}