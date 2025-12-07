package utn.back.mordiscoapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.HorarioRepository;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Scheduler inteligente que desactiva promociones considerando horarios de atención
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromocionScheduler {

    private final PromocionRepository promocionRepository;
    private final RestauranteRepository restauranteRepository;
    private final HorarioRepository horarioRepository;

    /**
     * ⏰ Se ejecuta cada 15 minutos para verificar promociones vencidas
     * según el horario de cierre de cada restaurante
     * Cron: "0 0/15 * * * *" = cada 15 minutos
     */
    @Scheduled(cron = "0 0/15 * * * *")
    @Transactional
    public void desactivarPromocionesVencidasPorHorario() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        DayOfWeek diaActual = LocalDate.now().getDayOfWeek();

        try {
            List<Restaurante> restaurantes = restauranteRepository.findAll();

            for (Restaurante restaurante : restaurantes) {
                // Obtener horarios del restaurante para hoy
                List<HorarioAtencion> horariosHoy = horarioRepository
                        .findAllByRestauranteId(restaurante.getId())
                        .stream()
                        .filter(h -> h.getDia().equals(diaActual))
                        .toList();

                // Si el restaurante está cerrado o pasó su horario de cierre
                if (restauranteCerrado(horariosHoy, ahora)) {
                    desactivarPromocionesVencidasRestaurante(
                            restaurante.getId(),
                            hoy
                    );
                }
            }

        } catch (Exception e) {
            log.error("❌ Error en scheduler de promociones por horario", e);
        }
    }

    /**
     * ⏰ FALLBACK: Se ejecuta a las 00:05 para desactivar promociones
     * de restaurantes sin horarios configurados o que quedaron pendientes
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void desactivarPromocionesVencidasFallback() {
        LocalDate hoy = LocalDate.now();

        try {
            // Desactiva TODAS las promociones vencidas (sin importar horarios)
            promocionRepository.desactivarPromocionesVencidas(hoy);
        } catch (Exception e) {
            log.error("❌ Error en fallback de promociones", e);
        }
    }

    /**
     * Desactiva promociones vencidas de UN restaurante específico
     */
    private void desactivarPromocionesVencidasRestaurante(Long restauranteId, LocalDate fecha) {
        // Obtener promociones activas del restaurante que ya vencieron
        List<Promocion> promocionesVencidas = promocionRepository
                .findByRestauranteIdAndActivaTrueAndFechaFinBefore(restauranteId, fecha);

        if (promocionesVencidas.isEmpty()) {
            return;
        }

        // Desactivar cada promoción
        promocionesVencidas.forEach(promo -> promo.setActiva(false));
        promocionRepository.saveAll(promocionesVencidas);

    }

    /**
     * Verifica si el restaurante está cerrado en este momento
     */
    private boolean restauranteCerrado(List<HorarioAtencion> horariosHoy, LocalTime ahora) {
        if (horariosHoy.isEmpty()) {
            // Sin horarios configurados = asumimos que está abierto
            return false;
        }

        // Verificar si estamos dentro de algún rango de atención
        for (HorarioAtencion horario : horariosHoy) {
            if (horario.getCruzaMedianoche()) {
                // Si ahora es >= hora apertura O ahora < hora cierre
                if (ahora.isAfter(horario.getHoraApertura()) ||
                        ahora.isBefore(horario.getHoraCierre())) {
                    return false; // Está abierto
                }
            } else {
                if (ahora.isAfter(horario.getHoraApertura()) &&
                        ahora.isBefore(horario.getHoraCierre())) {
                    return false; // Está abierto
                }
            }
        }

        return true;
    }
}