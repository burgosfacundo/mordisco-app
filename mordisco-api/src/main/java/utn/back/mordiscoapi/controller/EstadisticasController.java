package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utn.back.mordiscoapi.model.dto.estadisticas.AdminEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RepartidorEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RestauranteEstadisticasDTO;
import utn.back.mordiscoapi.service.interf.IEstadisticasAdminService;
import utn.back.mordiscoapi.service.interf.IEstadisticasRepartidorService;
import utn.back.mordiscoapi.service.interf.IEstadisticasRestauranteService;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Estadísticas", description = "Endpoints para estadísticas por rol")
public class EstadisticasController {

    private final IEstadisticasAdminService estadisticasAdminService;
    private final IEstadisticasRestauranteService estadisticasRestauranteService;
    private final IEstadisticasRepartidorService estadisticasRepartidorService;

    /**
     * Obtiene las estadísticas generales de la plataforma para administradores
     * @return Estadísticas del administrador
     */
    @Operation(
            summary = "Obtener estadísticas de administrador",
            description = "Retorna estadísticas generales de la plataforma: usuarios totales, " +
                    "pedidos, ingresos, métodos de pago, restaurantes y repartidores más activos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo administradores")
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminEstadisticasDTO> getEstadisticasAdmin() {
        return ResponseEntity.ok(estadisticasAdminService.getEstadisticas());
    }

    /**
     * Obtiene las estadísticas de un restaurante
     * @param id ID del restaurante
     * @return Estadísticas del restaurante
     */
    @Operation(
            summary = "Obtener estadísticas de restaurante",
            description = "Retorna estadísticas del restaurante: ingresos totales y por período, " +
                    "productos más vendidos, tiempo promedio de preparación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo restaurantes")
    })
    @GetMapping("/restaurante/{id}")
    @PreAuthorize("hasRole('RESTAURANTE')")
    public ResponseEntity<RestauranteEstadisticasDTO> getEstadisticasRestaurante(@PathVariable Long id) {
        return ResponseEntity.ok(estadisticasRestauranteService.getEstadisticas(id));
    }

    /**
     * Obtiene las estadísticas de un repartidor
     * @param id ID del repartidor
     * @return Estadísticas del repartidor
     */
    @Operation(
            summary = "Obtener estadísticas de repartidor",
            description = "Retorna estadísticas del repartidor: ganancias totales y por período, " +
                    "tiempo promedio de entrega, pedidos por día/semana/mes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo repartidores")
    })
    @GetMapping("/repartidor/{id}")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<RepartidorEstadisticasDTO> getEstadisticasRepartidor(@PathVariable Long id) {
        return ResponseEntity.ok(estadisticasRepartidorService.getEstadisticas(id));
    }
}
