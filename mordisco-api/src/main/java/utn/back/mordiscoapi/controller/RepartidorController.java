package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.repartidor.*;
import utn.back.mordiscoapi.service.interf.IRepartidorService;

import java.util.List;

@Tag(name = "Repartidor", description = "Gestión de repartidores y entregas")
@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private final IRepartidorService repartidorService;

    /**
     * Buscar repartidores disponibles cerca de una ubicación
     */
    @Operation(
            summary = "Buscar repartidores disponibles cercanos",
            description = """
            Encuentra repartidores disponibles en un radio específico desde una ubicación.
            Útil para:
            - Asignar automáticamente repartidores a pedidos
            - Mostrar repartidores disponibles en un área
            **Rol necesario: ADMIN o RESTAURANTE**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @GetMapping("/disponibles-cercanos")
    public ResponseEntity<List<RepartidorResponseDTO>> getDisponiblesCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "10.0") Double radioKm) {

        List<RepartidorResponseDTO> repartidores = repartidorService
                .findDisponiblesCercanos(latitud, longitud, radioKm);

        return ResponseEntity.ok(repartidores);
    }


    /**
     * Traer todos los pedidos asignados a un repartidor
     */
    @Operation(
            summary = "Traer todos los pedidos asignados a un repartidor",
            description = """
            Encuentra los pedidos entregados o pendientes a entregar de un repartidor especifico
            Útil para:
            - Mostrar el historial de pedidos de un repartidor espacifico
            **Rol necesario: ADMIN o REPARTIDOR**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Page<PedidoResponseDTO>> getAllPedidosRepartidor(
            @RequestParam int page,
            @RequestParam int size,
            @PathVariable Long id ) throws NotFoundException{
        return ResponseEntity.ok(repartidorService.findPedidosByRepartidor(page,size,id));
    }

    /**
     * Traer todos los pedidos en camino asignados a un repartidor
     */
    @Operation(
            summary = "Traer todos los pedidos en camino asignados a un repartidor",
            description = """
            Encuentra los pedidos pendientes a entregar de un repartidor especifico
            Útil para:
            - Mostrar el historial de pedidos de un repartidor espacifico
            **Rol necesario: ADMIN o REPARTIDOR**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    @GetMapping("/a-entregar/{id}")
    public ResponseEntity<Page<PedidoResponseDTO>> getPedidosAEntregarRepartidor(
            @RequestParam int page,
            @RequestParam int size,
            @PathVariable Long id ) throws NotFoundException{
        return ResponseEntity.ok(repartidorService.findPedidosByRepartidor_EnCamino(page,size,id));
    }



    /**
     * Obtener estadísticas del repartidor
     */
    @Operation(
            summary = "Obtener estadísticas del repartidor",
            description = """
            Retorna estadísticas de entregas y ganancias del repartidor.
            **Incluye:**
            - Total de entregas completadas
            - Total ganado
            - Calificación promedio
            - Entregas por período (hoy, semana, mes)
            **Rol necesario: ADMIN o REPARTIDOR (propio)**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Repartidor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('REPARTIDOR') and @usuarioSecurity.puedeAccederAUsuario(#id))")
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<RepartidorEstadisticasDTO> getEstadisticas(@PathVariable Long id)
            throws NotFoundException {

        return ResponseEntity.ok(repartidorService.getEstadisticas(id));
    }
}