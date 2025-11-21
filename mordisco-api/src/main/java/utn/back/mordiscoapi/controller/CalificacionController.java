package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.calificaciones.*;
import utn.back.mordiscoapi.service.impl.CalificacionServiceImpl;


@Tag(name = "Calificaciones", description = "Gestión de calificaciones de pedidos y repartidores")
@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionServiceImpl calificacionService;

    // ========== CALIFICACIONES DE PEDIDO ==========

    @Operation(
            summary = "Calificar pedido",
            description = """
            Permite al cliente calificar un pedido ya recibido.
            **Califica:** Calidad de comida, tiempo de entrega y packaging.
            **Requisitos:**
            - El pedido debe estar en estado RECIBIDO
            - Solo el cliente que hizo el pedido puede calificarlo
            - Cada pedido solo puede ser calificado una vez
            **Rol necesario: CLIENTE**
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Calificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Pedido no puede ser calificado o ya fue calificado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/pedido")
    public ResponseEntity<CalificacionPedidoResponseDTO> calificarPedido(
            @Valid @RequestBody CalificacionPedidoRequestDTO dto)
            throws NotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(calificacionService.calificarPedido(dto));
    }

    @Operation(
            summary = "Obtener calificación de un pedido",
            description = "Retorna la calificación de un pedido específico si existe"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calificación encontrada"),
            @ApiResponse(responseCode = "404", description = "El pedido no tiene calificación")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<CalificacionPedidoResponseDTO> getCalificacionPedido(
            @PathVariable Long pedidoId) throws NotFoundException {
        return ResponseEntity.ok(calificacionService.getCalificacionPedido(pedidoId));
    }

    @Operation(
            summary = "Obtener calificaciones de un restaurante",
            description = """
            Retorna todas las calificaciones de pedidos de un restaurante.
            Útil para mostrar opiniones de clientes sobre el restaurante.
        """
    )
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<Page<CalificacionPedidoResponseDTO>> getCalificacionesRestaurante(
            @PathVariable Long restauranteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                calificacionService.getCalificacionesRestaurante(restauranteId, page, size)
        );
    }

    @Operation(
            summary = "Obtener estadísticas de un restaurante",
            description = "Retorna promedios y totales de calificaciones del restaurante"
    )
    @GetMapping("/restaurante/{restauranteId}/estadisticas")
    public ResponseEntity<EstadisticasRestauranteDTO> getEstadisticasRestaurante(
            @PathVariable Long restauranteId) throws NotFoundException {
        return ResponseEntity.ok(calificacionService.getEstadisticasRestaurante(restauranteId));
    }

    @Operation(
            summary = "Eliminar calificación de pedido",
            description = "Solo el autor de la calificación puede eliminarla"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/pedido/{calificacionId}")
    public ResponseEntity<Void> eliminarCalificacionPedido(@PathVariable Long calificacionId)
            throws NotFoundException, BadRequestException {
        calificacionService.eliminarCalificacionPedido(calificacionId);
        return ResponseEntity.noContent().build();
    }

    // ========== CALIFICACIONES DE REPARTIDOR ==========

    @Operation(
            summary = "Calificar repartidor",
            description = """
            Permite al cliente calificar al repartidor que entregó su pedido.
            **Califica:** Atención, comunicación y profesionalismo.
            **Requisitos:**
            - El pedido debe estar en estado RECIBIDO
            - El pedido debe tener un repartidor asignado
            - Solo el cliente puede calificar
            - Cada repartidor solo puede ser calificado una vez por pedido
            **Rol necesario: CLIENTE**
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Calificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Repartidor no puede ser calificado o ya fue calificado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/repartidor")
    public ResponseEntity<CalificacionRepartidorResponseDTO> calificarRepartidor(
            @Valid @RequestBody CalificacionRepartidorRequestDTO dto)
            throws NotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(calificacionService.calificarRepartidor(dto));
    }

    @Operation(
            summary = "Obtener calificaciones de un repartidor",
            description = "Retorna todas las calificaciones recibidas por un repartidor"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<Page<CalificacionRepartidorResponseDTO>> getCalificacionesRepartidor(
            @PathVariable Long repartidorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                calificacionService.getCalificacionesRepartidor(repartidorId, page, size)
        );
    }

    @Operation(
            summary = "Obtener estadísticas de un repartidor",
            description = "Retorna promedios y totales de calificaciones del repartidor"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR')")
    @GetMapping("/repartidor/{repartidorId}/estadisticas")
    public ResponseEntity<EstadisticasRepartidorDTO> getEstadisticasRepartidor(
            @PathVariable Long repartidorId) throws NotFoundException {
        return ResponseEntity.ok(calificacionService.getEstadisticasRepartidor(repartidorId));
    }

    @Operation(
            summary = "Eliminar calificación de repartidor",
            description = "Solo el autor de la calificación puede eliminarla"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/repartidor/{calificacionId}")
    public ResponseEntity<Void> eliminarCalificacionRepartidor(@PathVariable Long calificacionId)
            throws NotFoundException, BadRequestException {
        calificacionService.eliminarCalificacionRepartidor(calificacionId);
        return ResponseEntity.noContent().build();
    }
}