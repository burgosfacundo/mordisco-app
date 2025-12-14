package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.model.dto.estadisticas.GananciasPorPeriodoDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.PedidosPorPeriodoDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RepartidorEstadisticasDTO;
import utn.back.mordiscoapi.repository.GananciaRepartidorRepository;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.service.interf.IEstadisticasRepartidorService;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasRepartidorServiceImpl implements IEstadisticasRepartidorService {

    private final GananciaRepartidorRepository gananciaRepartidorRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    public RepartidorEstadisticasDTO getEstadisticas(Long repartidorId) {
        // 1. Calcular ganancias totales
        BigDecimal gananciasTotales = gananciaRepartidorRepository.calcularTotalGanancias(repartidorId);

        // 2. Obtener ganancias por período (últimos 12 meses)
        List<GananciasPorPeriodoDTO> gananciasPorPeriodo = obtenerGananciasPorPeriodo(repartidorId);

        // 3. Calcular tiempo promedio de entrega
        Double tiempoPromedio = pedidoRepository.calcularTiempoPromedioEntregaRepartidor(repartidorId);
        if (tiempoPromedio == null) {
            tiempoPromedio = 0.0;
        }

        // 4. Obtener pedidos por día (últimos 30 días)
        List<PedidosPorPeriodoDTO> pedidosPorDia = obtenerPedidosPorDia(repartidorId);

        // 5. Obtener pedidos por semana (últimas 12 semanas)
        List<PedidosPorPeriodoDTO> pedidosPorSemana = obtenerPedidosPorSemana(repartidorId);

        // 6. Obtener pedidos por mes (últimos 12 meses)
        List<PedidosPorPeriodoDTO> pedidosPorMes = obtenerPedidosPorMes(repartidorId);

        return new RepartidorEstadisticasDTO(
                gananciasTotales,
                gananciasPorPeriodo,
                tiempoPromedio,
                pedidosPorDia,
                pedidosPorSemana,
                pedidosPorMes
        );
    }

    @Override
    public RepartidorEstadisticasDTO getEstadisticasByUsuarioId(Long usuarioId) {
        // Para repartidores, el usuarioId es el mismo que el repartidorId
        // No hay una entidad separada para repartidor, es directamente el usuario
        return getEstadisticas(usuarioId);
    }

    private List<GananciasPorPeriodoDTO> obtenerGananciasPorPeriodo(Long repartidorId) {
        List<Object[]> results = gananciaRepartidorRepository.calcularGananciasPorMes(repartidorId);

        return results.stream()
                .map(row -> new GananciasPorPeriodoDTO(
                        (String) row[0],           // periodo
                        (BigDecimal) row[1]        // ganancias
                ))
                .toList();
    }

    private List<PedidosPorPeriodoDTO> obtenerPedidosPorDia(Long repartidorId) {
        List<Object[]> results = pedidoRepository.findPedidosPorDiaRepartidor(repartidorId);

        return results.stream()
                .map(row -> new PedidosPorPeriodoDTO(
                        ((Date) row[0]).toString(),    // periodo (fecha)
                        ((Number) row[1]).intValue()   // cantidad
                ))
                .toList();
    }

    private List<PedidosPorPeriodoDTO> obtenerPedidosPorSemana(Long repartidorId) {
        List<Object[]> results = pedidoRepository.findPedidosPorSemanaRepartidor(repartidorId);

        return results.stream()
                .map(row -> new PedidosPorPeriodoDTO(
                        (String) row[0],               // periodo (año-semana)
                        ((Number) row[1]).intValue()   // cantidad
                ))
                .toList();
    }

    private List<PedidosPorPeriodoDTO> obtenerPedidosPorMes(Long repartidorId) {
        List<Object[]> results = pedidoRepository.findPedidosPorMesRepartidor(repartidorId);

        return results.stream()
                .map(row -> new PedidosPorPeriodoDTO(
                        (String) row[0],               // periodo (año-mes)
                        ((Number) row[1]).intValue()   // cantidad
                ))
                .toList();
    }
}
