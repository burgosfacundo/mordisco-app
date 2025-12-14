package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.GananciaRepartidorMapper;
import utn.back.mordiscoapi.model.dto.ganancia.GananciaRepartidorResponseDTO;
import utn.back.mordiscoapi.model.dto.ganancia.TotalesGananciaDTO;
import utn.back.mordiscoapi.model.entity.ConfiguracionSistema;
import utn.back.mordiscoapi.model.entity.GananciaRepartidor;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.repository.ConfiguracionSistemaRepository;
import utn.back.mordiscoapi.repository.GananciaRepartidorRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IGananciaRepartidorService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Service
@RequiredArgsConstructor
public class GananciaRepartidorServiceImpl implements IGananciaRepartidorService {

    private final GananciaRepartidorRepository gananciaRepository;
    private final ConfiguracionSistemaRepository configuracionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @Override
    public void registrarGanancia(Pedido pedido) throws NotFoundException {
        if (pedido.getRepartidor() == null) {
            throw new NotFoundException("El pedido no tiene repartidor asignado");
        }

        // Obtener configuración actual
        ConfiguracionSistema config = configuracionRepository.findConfiguracionActual()
                .orElseThrow(() -> new NotFoundException("Configuración del sistema no encontrada"));

        // Usar el costo de delivery del pedido
        BigDecimal costoDelivery = pedido.getCostoDelivery();
        
        // Si no tiene costo de delivery calculado, usar valor por defecto
        if (costoDelivery == null || costoDelivery.compareTo(BigDecimal.ZERO) <= 0) {
            costoDelivery = config.getCostoBaseDelivery();
        }

        // Calcular ganancia del repartidor (80% del costo de delivery)
        BigDecimal gananciaRepartidor = config.calcularGananciaRepartidor(costoDelivery);

        // Calcular comisión de la plataforma (20% del costo de delivery)
        BigDecimal comisionPlataforma = costoDelivery.subtract(gananciaRepartidor);

        // Crear registro de ganancia
        GananciaRepartidor ganancia = GananciaRepartidor.builder()
                .pedido(pedido)
                .repartidor(pedido.getRepartidor())
                .costoDelivery(costoDelivery)
                .comisionPlataforma(comisionPlataforma)
                .gananciaRepartidor(gananciaRepartidor)
                .porcentajeAplicado(config.getPorcentajeGananciasRepartidor())
                .build();

        gananciaRepository.save(ganancia);
    }

    @Override
    public Page<GananciaRepartidorResponseDTO> getGanancias(
            Long repartidorId,
            int page,
            int size) throws NotFoundException {

        if (!usuarioRepository.existsById(repartidorId)) {
            throw new NotFoundException("Repartidor no encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);
        return gananciaRepository
                .findByRepartidor_IdOrderByFechaRegistroDesc(repartidorId, pageable)
                .map(GananciaRepartidorMapper::toDTO);
    }

    @Override
    public Page<GananciaRepartidorResponseDTO> getGananciasEnRango(
            Long repartidorId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            int page,
            int size) throws NotFoundException {

        if (!usuarioRepository.existsById(repartidorId)) {
            throw new NotFoundException("Repartidor no encontrado");
        }

        Pageable pageable = PageRequest.of(page, size);
        return gananciaRepository
                .findByRepartidorAndFechaRange(repartidorId, fechaInicio, fechaFin, pageable)
                .map(GananciaRepartidorMapper::toDTO);
    }

    @Override
    public TotalesGananciaDTO getTotales(Long repartidorId) throws NotFoundException {
        if (!usuarioRepository.existsById(repartidorId)) {
            throw new NotFoundException("Repartidor no encontrado");
        }

        // Total acumulado
        BigDecimal totalAcumulado = gananciaRepository.calcularTotalGanancias(repartidorId);

        // Total del mes actual
        LocalDateTime inicioMes = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        BigDecimal totalMes = gananciaRepository.calcularGananciasEnRango(repartidorId, inicioMes, finMes);

        // Total de la semana actual (lunes a domingo)
        LocalDateTime inicioSemana = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finSemana = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59);
        BigDecimal totalSemana = gananciaRepository.calcularGananciasEnRango(repartidorId, inicioSemana, finSemana);

        // Cantidad de entregas y promedio
        Long cantidadEntregas = gananciaRepository.contarEntregas(repartidorId);
        BigDecimal promedioEntrega = cantidadEntregas > 0
                ? totalAcumulado.divide(BigDecimal.valueOf(cantidadEntregas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new TotalesGananciaDTO(
                totalAcumulado,
                totalMes,
                totalSemana,
                promedioEntrega,
                cantidadEntregas
        );
    }
}
