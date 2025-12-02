package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.model.dto.estadisticas.IngresosPorPeriodoDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.ProductoMasVendidoDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RestauranteEstadisticasDTO;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IEstadisticasRestauranteService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasRestauranteServiceImpl implements IEstadisticasRestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    public RestauranteEstadisticasDTO getEstadisticas(Long restauranteId) {
        // 1. Calcular ingresos totales
        BigDecimal ingresosTotales = restauranteRepository.calcularIngresosTotalesRestaurante(restauranteId);

        // 2. Obtener ingresos por período (últimos 12 meses)
        List<IngresosPorPeriodoDTO> ingresosPorPeriodo = obtenerIngresosPorPeriodo(restauranteId);

        // 3. Obtener productos más vendidos
        List<ProductoMasVendidoDTO> productosMasVendidos = obtenerProductosMasVendidos(restauranteId);

        // 4. Calcular tiempo promedio de preparación
        Double tiempoPromedio = pedidoRepository.calcularTiempoPromedioPreparacion(restauranteId);
        // Si no hay datos, retornar 0.0
        if (tiempoPromedio == null) {
            tiempoPromedio = 0.0;
        }

        return new RestauranteEstadisticasDTO(
                ingresosTotales,
                ingresosPorPeriodo,
                productosMasVendidos,
                tiempoPromedio
        );
    }

    private List<IngresosPorPeriodoDTO> obtenerIngresosPorPeriodo(Long restauranteId) {
        List<Object[]> results = restauranteRepository.calcularIngresosPorMes(restauranteId);

        return results.stream()
                .map(row -> new IngresosPorPeriodoDTO(
                        (String) row[0],           // periodo
                        (BigDecimal) row[1]        // ingresos
                ))
                .toList();
    }

    private List<ProductoMasVendidoDTO> obtenerProductosMasVendidos(Long restauranteId) {
        List<Object[]> results = productoRepository.findProductosMasVendidos(restauranteId);

        return results.stream()
                .map(row -> new ProductoMasVendidoDTO(
                        ((Number) row[0]).longValue(),    // productoId
                        (String) row[1],                   // nombre
                        ((Number) row[2]).intValue(),      // cantidadVendida
                        (BigDecimal) row[3]                // ingresoGenerado
                ))
                .toList();
    }
}
