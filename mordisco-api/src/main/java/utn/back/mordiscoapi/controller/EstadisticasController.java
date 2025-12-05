package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.estadisticas.AdminEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RepartidorEstadisticasDTO;
import utn.back.mordiscoapi.model.dto.estadisticas.RestauranteEstadisticasDTO;
import utn.back.mordiscoapi.security.jwt.utils.AuthUtils;
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
    private final AuthUtils authUtils;

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
     * Obtiene las estadísticas del restaurante del usuario autenticado
     * @return Estadísticas del restaurante
     */
    @Operation(
            summary = "Obtener estadísticas de restaurante",
            description = "Retorna estadísticas del restaurante autenticado: ingresos totales y por período, " +
                    "productos más vendidos, tiempo promedio de preparación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo restaurantes"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @GetMapping("/restaurante")
    @PreAuthorize("hasRole('RESTAURANTE')")
    public ResponseEntity<RestauranteEstadisticasDTO> getEstadisticasRestaurante() throws NotFoundException {
        Long usuarioId = authUtils.getUsuarioAutenticado().orElseThrow(
                () -> new AccessDeniedException("No tienes permiso para esta acción")
        ).getId();
        return ResponseEntity.ok(estadisticasRestauranteService.getEstadisticasByUsuarioId(usuarioId));
    }

    /**
     * Obtiene las estadísticas del repartidor autenticado
     * @return Estadísticas del repartidor
     */
    @Operation(
            summary = "Obtener estadísticas de repartidor",
            description = "Retorna estadísticas del repartidor autenticado: ganancias totales y por período, " +
                    "tiempo promedio de entrega, pedidos por día/semana/mes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo repartidores")
    })
    @GetMapping("/repartidor")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<RepartidorEstadisticasDTO> getEstadisticasRepartidor() {
        Long usuarioId = authUtils.getUsuarioAutenticado().orElseThrow(
                () -> new AccessDeniedException("No tienes permiso para esta acción")
        ).getId();
        return ResponseEntity.ok(estadisticasRepartidorService.getEstadisticasByUsuarioId(usuarioId));
    }
}
