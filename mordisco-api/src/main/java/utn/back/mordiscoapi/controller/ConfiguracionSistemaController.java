package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaRequestDTO;
import utn.back.mordiscoapi.model.dto.configuracion.ConfiguracionSistemaResponseDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.service.interf.IConfiguracionSistemaService;

import java.math.BigDecimal;

@Tag(name = "Configuración Sistema", description = "Gestión de la configuración global del sistema")
@RestController
@RequestMapping("/api/configuraciones")
@RequiredArgsConstructor
public class ConfiguracionSistemaController {

    private final IConfiguracionSistemaService configuracionService;

    /**
     * Obtiene la configuración actual del sistema
     */
    @Operation(
            summary = "Obtener configuración del sistema",
            description = "Retorna la configuración global actual del sistema Mordisco. " +
                    "**Rol necesario: ADMIN**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ConfiguracionSistemaResponseDTO> getConfiguracion() {
        return ResponseEntity.ok(configuracionService.getConfiguracionActual());
    }

    /**
     * Actualiza la configuración del sistema
     */
    @Operation(
            summary = "Actualizar configuración del sistema",
            description = """
            Actualiza los parámetros globales del sistema.
            **Parámetros configurables:**
            - Comisión de la plataforma (0-30%)
            - Radio máximo de entrega (1-50 km)
            - Tiempo máximo de entrega (15-180 min)
            - Costos de delivery
            - Monto mínimo de pedido
            - Porcentaje de ganancias del repartidor (0-100%)
            - Modo mantenimiento
            **Rol necesario: ADMIN**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de configuración inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<ConfiguracionSistemaResponseDTO> actualizarConfiguracion(
            @RequestBody @Valid ConfiguracionSistemaRequestDTO dto,
            Authentication authentication) throws NotFoundException {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        ConfiguracionSistemaResponseDTO response = configuracionService
                .actualizarConfiguracion(dto, usuario.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Calcula el costo de delivery para una distancia
     */
    @Operation(
            summary = "Calcular costo de delivery",
            description = "Calcula el costo de delivery basado en la distancia en kilómetros. " +
                    "Fórmula: Costo Base + (Distancia × Costo por Km)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Costo calculado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Distancia inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/calcular-delivery")
    public ResponseEntity<BigDecimal> calcularCostoDelivery(
            @RequestParam BigDecimal distanciaKm) {

        BigDecimal costo = configuracionService.calcularCostoDelivery(distanciaKm);
        return ResponseEntity.ok(costo);
    }

    /**
     * Obtiene el monto mínimo de pedido
     */
    @Operation(
            summary = "Obtener monto mínimo de pedido",
            description = "Retorna el monto mínimo configurado para realizar un pedido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Monto obtenido exitosamente")
    })
    @GetMapping("/monto-minimo")
    public ResponseEntity<BigDecimal> getMontoMinimo() {
        return ResponseEntity.ok(configuracionService.getMontoMinimoPedido());
    }

    /**
     * Verifica si el sistema está en mantenimiento
     */
    @Operation(
            summary = "Verificar estado de mantenimiento",
            description = "Retorna true si el sistema está en modo mantenimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente")
    })
    @GetMapping("/mantenimiento")
    public ResponseEntity<Boolean> isEnMantenimiento() {
        return ResponseEntity.ok(configuracionService.isEnMantenimiento());
    }
}