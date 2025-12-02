package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.model.dto.estadisticas.*;
import utn.back.mordiscoapi.repository.PedidoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IEstadisticasAdminService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasAdminServiceImpl implements IEstadisticasAdminService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final RestauranteRepository restauranteRepository;

    // IDs de roles (deberían coincidir con los de la BD)
    private static final Long ROL_CLIENTE_ID = 1L;
    private static final Long ROL_RESTAURANTE_ID = 2L;
    private static final Long ROL_REPARTIDOR_ID = 3L;

    @Override
    public AdminEstadisticasDTO getEstadisticas() {
        // 1. Obtener totales de usuarios por rol
        UsuariosTotalesDTO usuariosTotales = obtenerUsuariosTotales();

        // 2. Total de pedidos completados
        Integer totalPedidos = pedidoRepository.countTotalPedidosCompletados();

        // 3. Ingresos totales de la plataforma
        BigDecimal ingresosTotales = pedidoRepository.calcularIngresosTotalesPlataforma();

        // 4. Métodos de pago más usados
        List<MetodoPagoEstadisticaDTO> metodosPago = obtenerMetodosPagoMasUsados();

        // 5. Restaurantes más activos
        List<RestauranteMasActivoDTO> restaurantesMasActivos = obtenerRestaurantesMasActivos();

        // 6. Repartidores más activos
        List<RepartidorMasActivoDTO> repartidoresMasActivos = obtenerRepartidoresMasActivos();

        return new AdminEstadisticasDTO(
                usuariosTotales,
                totalPedidos,
                ingresosTotales,
                metodosPago,
                restaurantesMasActivos,
                repartidoresMasActivos
        );
    }

    private UsuariosTotalesDTO obtenerUsuariosTotales() {
        Integer clientes = usuarioRepository.countByRolId(ROL_CLIENTE_ID);
        Integer restaurantes = usuarioRepository.countByRolId(ROL_RESTAURANTE_ID);
        Integer repartidores = usuarioRepository.countByRolId(ROL_REPARTIDOR_ID);
        Integer total = clientes + restaurantes + repartidores;

        return new UsuariosTotalesDTO(clientes, restaurantes, repartidores, total);
    }

    private List<MetodoPagoEstadisticaDTO> obtenerMetodosPagoMasUsados() {
        List<Object[]> results = pedidoRepository.findMetodosPagoMasUsados();

        return results.stream()
                .map(row -> new MetodoPagoEstadisticaDTO(
                        (String) row[0],           // metodoPago
                        ((Number) row[1]).intValue(), // cantidad
                        ((Number) row[2]).doubleValue() // porcentaje
                ))
                .toList();
    }

    private List<RestauranteMasActivoDTO> obtenerRestaurantesMasActivos() {
        List<Object[]> results = restauranteRepository.findRestaurantesMasActivos();

        return results.stream()
                .map(row -> new RestauranteMasActivoDTO(
                        ((Number) row[0]).longValue(),    // restauranteId
                        (String) row[1],                   // nombre
                        ((Number) row[2]).intValue(),      // pedidosCompletados
                        (BigDecimal) row[3]                // ingresoGenerado
                ))
                .toList();
    }

    private List<RepartidorMasActivoDTO> obtenerRepartidoresMasActivos() {
        List<Object[]> results = usuarioRepository.findRepartidoresMasActivos();

        return results.stream()
                .map(row -> {
                    String nombre = row[1] + " " + row[2]; // nombre + apellido
                    return new RepartidorMasActivoDTO(
                            ((Number) row[0]).longValue(),    // repartidorId
                            nombre,                            // nombre completo
                            ((Number) row[3]).intValue(),      // entregasRealizadas
                            (BigDecimal) row[4]                // gananciaGenerada
                    );
                })
                .toList();
    }
}
