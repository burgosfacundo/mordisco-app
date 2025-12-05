package utn.back.mordiscoapi.common.util;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Validador de horarios de atención de restaurantes.
 * Verifica que no haya solapamientos ni duplicados.
 * Soporta horarios que cruzan la medianoche (ej: 22:00 - 02:00).
 */
@UtilityClass
public class HorarioValidator {

    /**
     * Valida que un nuevo horario no se solape con los existentes
     *
     * @param dia Día de la semana del nuevo horario
     * @param horaApertura Hora de apertura del nuevo horario
     * @param horaCierre Hora de cierre del nuevo horario
     * @param cruzaMedianoche Indica si el horario cruza la medianoche
     * @param horariosExistentes Lista de horarios ya registrados para el restaurante
     * @param horarioIdExcluir ID del horario a excluir en caso de actualización (null si es creación)
     * @throws IllegalArgumentException si hay conflicto
     */
    public void validarNoSolapamiento(
            DayOfWeek dia,
            LocalTime horaApertura,
            LocalTime horaCierre,
            Boolean cruzaMedianoche,
            List<HorarioAtencion> horariosExistentes,
            Long horarioIdExcluir
    ) {
        // Validar coherencia de horarios según si cruza medianoche
        if (cruzaMedianoche != null && cruzaMedianoche) {
            // Si cruza medianoche: apertura debe ser posterior a cierre (ej: 22:00 > 02:00)
            if (!horaApertura.isAfter(horaCierre)) {
                throw new IllegalArgumentException(
                        "Para horarios nocturnos, la hora de apertura debe ser posterior a la hora de cierre. " +
                        "Ejemplo: apertura 22:00, cierre 02:00 (del día siguiente)"
                );
            }
        } else {
            // Si NO cruza medianoche: apertura < cierre (lógica tradicional)
            if (!horaApertura.isBefore(horaCierre)) {
                throw new IllegalArgumentException(
                        "La hora de apertura debe ser anterior a la hora de cierre"
                );
            }
        }

        boolean cruzaMedianocheValue = cruzaMedianoche != null && cruzaMedianoche;

        // Filtrar horarios del mismo día (excluyendo el actual si es update)
        List<HorarioAtencion> horariosMismoDia = horariosExistentes.stream()
                .filter(h -> h.getDia().equals(dia))
                .filter(h -> !h.getId().equals(horarioIdExcluir))
                .toList();

        // Verificar solapamiento con cada horario existente
        for (HorarioAtencion existente : horariosMismoDia) {
            if (haySolapamiento(horaApertura, horaCierre, cruzaMedianocheValue, existente)) {
                String mensajeExistente = formatearHorario(existente);
                throw new IllegalArgumentException(
                        String.format(
                                "El horario se solapa con un horario existente: %s",
                                mensajeExistente
                        )
                );
            }
        }
    }

    /**
     * Verifica si dos rangos horarios se solapan.
     * Considera 4 casos:
     * 1. Ninguno cruza medianoche
     * 2. Solo el nuevo cruza medianoche
     * 3. Solo el existente cruza medianoche
     * 4. Ambos cruzan medianoche
     */
    private boolean haySolapamiento(
            LocalTime nuevaApertura,
            LocalTime nuevoCierre,
            boolean nuevaCruzaMedianoche,
            HorarioAtencion existente
    ) {
        LocalTime existenteApertura = existente.getHoraApertura();
        LocalTime existenteCierre = existente.getHoraCierre();
        boolean existenteCruzaMedianoche = existente.getCruzaMedianoche() != null && existente.getCruzaMedianoche();

        // Caso 1: Ninguno cruza medianoche (lógica tradicional)
        if (!nuevaCruzaMedianoche && !existenteCruzaMedianoche) {
            return haySolapamientoNormal(nuevaApertura, nuevoCierre, existenteApertura, existenteCierre);
        }

        // Caso 2: Solo el nuevo cruza medianoche
        if (nuevaCruzaMedianoche && !existenteCruzaMedianoche) {
            return haySolapamientoNuevoNocturno(nuevaApertura, nuevoCierre, existenteApertura, existenteCierre);
        }

        // Caso 3: Solo el existente cruza medianoche
        if (!nuevaCruzaMedianoche && existenteCruzaMedianoche) {
            return haySolapamientoExistenteNocturno(nuevaApertura, nuevoCierre, existenteApertura, existenteCierre);
        }

        // Caso 4: Ambos cruzan medianoche
        return haySolapamientoAmbosNocturnos(nuevaApertura, nuevoCierre, existenteApertura, existenteCierre);
    }

    /**
     * Caso 1: Ninguno cruza medianoche
     * Ejemplo: Existente 10:00-18:00, Nuevo 12:00-14:00
     */
    private boolean haySolapamientoNormal(
            LocalTime nuevaApertura, LocalTime nuevoCierre,
            LocalTime existenteApertura, LocalTime existenteCierre
    ) {
        // El nuevo engloba al existente
        if (nuevaApertura.isBefore(existenteApertura) && nuevoCierre.isAfter(existenteCierre)) {
            return true;
        }

        // El nuevo empieza durante el existente
        if ((nuevaApertura.isAfter(existenteApertura) || nuevaApertura.equals(existenteApertura))
                && nuevaApertura.isBefore(existenteCierre)) {
            return true;
        }

        // El nuevo termina durante el existente
        if (nuevoCierre.isAfter(existenteApertura)
                && (nuevoCierre.isBefore(existenteCierre) || nuevoCierre.equals(existenteCierre))) {
            return true;
        }

        // El existente empieza durante el nuevo
        if (existenteApertura.isAfter(nuevaApertura) && existenteApertura.isBefore(nuevoCierre)) {
            return true;
        }

        return false;
    }

    /**
     * Caso 2: Solo el nuevo cruza medianoche
     * Ejemplo: Existente 10:00-18:00, Nuevo 22:00-02:00 (nocturno)
     */
    private boolean haySolapamientoNuevoNocturno(
            LocalTime nuevaApertura, LocalTime nuevoCierre,
            LocalTime existenteApertura, LocalTime existenteCierre
    ) {
        // El nuevo nocturno tiene dos segmentos: [nuevaApertura-23:59] y [00:00-nuevoCierre]
        
        // Verificar si el existente solapa con el segmento de la noche (apertura hasta medianoche)
        if (existenteApertura.isBefore(LocalTime.MAX) && existenteCierre.isAfter(nuevaApertura)) {
            return true;
        }

        // Verificar si el existente solapa con el segmento de la madrugada (medianoche hasta cierre)
        if (existenteApertura.isBefore(nuevoCierre) && existenteCierre.isAfter(LocalTime.MIN)) {
            return true;
        }

        return false;
    }

    /**
     * Caso 3: Solo el existente cruza medianoche
     * Ejemplo: Existente 22:00-02:00 (nocturno), Nuevo 10:00-18:00
     */
    private boolean haySolapamientoExistenteNocturno(
            LocalTime nuevaApertura, LocalTime nuevoCierre,
            LocalTime existenteApertura, LocalTime existenteCierre
    ) {
        // El existente nocturno tiene dos segmentos: [existenteApertura-23:59] y [00:00-existenteCierre]
        
        // Verificar si el nuevo solapa con el segmento de la noche del existente
        if (nuevaApertura.isBefore(LocalTime.MAX) && nuevoCierre.isAfter(existenteApertura)) {
            return true;
        }

        // Verificar si el nuevo solapa con el segmento de la madrugada del existente
        if (nuevaApertura.isBefore(existenteCierre) && nuevoCierre.isAfter(LocalTime.MIN)) {
            return true;
        }

        return false;
    }

    /**
     * Caso 4: Ambos cruzan medianoche
     * Ejemplo: Existente 22:00-02:00, Nuevo 23:00-03:00
     */
    private boolean haySolapamientoAmbosNocturnos(
            LocalTime nuevaApertura, LocalTime nuevoCierre,
            LocalTime existenteApertura, LocalTime existenteCierre
    ) {
        // Ambos tienen segmentos noche y madrugada
        
        // Solapamiento en segmento de noche (después de apertura hasta medianoche)
        if (nuevaApertura.isBefore(LocalTime.MAX) && existenteApertura.isBefore(LocalTime.MAX)) {
            // Si las aperturas están en el mismo rango nocturno
            if (nuevaApertura.isBefore(existenteApertura) || nuevaApertura.equals(existenteApertura)) {
                return true;
            }
            if (existenteApertura.isBefore(nuevaApertura)) {
                return true;
            }
        }

        // Solapamiento en segmento de madrugada (medianoche hasta cierre)
        if (nuevoCierre.isAfter(LocalTime.MIN) && existenteCierre.isAfter(LocalTime.MIN)) {
            // Si los cierres están en el mismo rango de madrugada
            if (nuevoCierre.isAfter(existenteCierre) || nuevoCierre.equals(existenteCierre)) {
                return true;
            }
            if (existenteCierre.isAfter(nuevoCierre)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Formatea un horario para mensajes de error
     */
    private String formatearHorario(HorarioAtencion horario) {
        String dia = formatearDia(horario.getDia());
        String apertura = horario.getHoraApertura().toString();
        String cierre = horario.getHoraCierre().toString();
        
        if (horario.getCruzaMedianoche() != null && horario.getCruzaMedianoche()) {
            DayOfWeek diaSiguiente = horario.getDia().plus(1);
            return String.format("%s %s - %s %s (nocturno)",
                    dia, apertura, formatearDia(diaSiguiente), cierre);
        } else {
            return String.format("%s %s - %s", dia, apertura, cierre);
        }
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