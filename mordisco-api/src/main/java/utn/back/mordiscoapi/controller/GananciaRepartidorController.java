package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.ganancia.GananciaRepartidorResponseDTO;
import utn.back.mordiscoapi.model.dto.ganancia.TotalesGananciaDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.service.interf.IGananciaRepartidorService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ganancias")
@RequiredArgsConstructor
@Tag(name = "Ganancias Repartidor", description = "Gestión de ganancias de repartidores")
public class GananciaRepartidorController {

    private final IGananciaRepartidorService gananciaService;

    @GetMapping("/repartidor/{id}")
    @PreAuthorize("hasAnyRole('REPARTIDOR', 'ADMIN')")
    @Operation(summary = "Obtener ganancias de un repartidor")
    public ResponseEntity<Page<GananciaRepartidorResponseDTO>> getGanancias(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) throws NotFoundException {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Validar que el repartidor solo pueda ver sus propias ganancias
        if (usuario.isRepartidor() && !usuario.getId().equals(id)) {
            throw new NotFoundException("No tienes permiso para ver estas ganancias");
        }

        Page<GananciaRepartidorResponseDTO> ganancias = gananciaService.getGanancias(id, page, size);
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/repartidor/{id}/rango")
    @PreAuthorize("hasAnyRole('REPARTIDOR', 'ADMIN')")
    @Operation(summary = "Obtener ganancias en un rango de fechas")
    public ResponseEntity<Page<GananciaRepartidorResponseDTO>> getGananciasEnRango(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) throws NotFoundException {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Validar que el repartidor solo pueda ver sus propias ganancias
        if (usuario.isRepartidor() && !usuario.getId().equals(id)) {
            throw new NotFoundException("No tienes permiso para ver estas ganancias");
        }

        Page<GananciaRepartidorResponseDTO> ganancias = gananciaService.getGananciasEnRango(
                id, fechaInicio, fechaFin, page, size
        );
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/repartidor/{id}/totales")
    @PreAuthorize("hasAnyRole('REPARTIDOR', 'ADMIN')")
    @Operation(summary = "Obtener totales de ganancias de un repartidor")
    public ResponseEntity<TotalesGananciaDTO> getTotales(
            @PathVariable Long id,
            Authentication authentication) throws NotFoundException {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Validar que el repartidor solo pueda ver sus propias ganancias
        if (usuario.isRepartidor() && !usuario.getId().equals(id)) {
            throw new NotFoundException("No tienes permiso para ver estas ganancias");
        }

        TotalesGananciaDTO totales = gananciaService.getTotales(id);
        return ResponseEntity.ok(totales);
    }

    @GetMapping("/mis-ganancias")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @Operation(summary = "Obtener mis ganancias (repartidor autenticado)")
    public ResponseEntity<Page<GananciaRepartidorResponseDTO>> getMisGanancias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) throws NotFoundException {

        Usuario repartidor = (Usuario) authentication.getPrincipal();
        Page<GananciaRepartidorResponseDTO> ganancias = gananciaService.getGanancias(
                repartidor.getId(), page, size
        );
        return ResponseEntity.ok(ganancias);
    }

    @GetMapping("/mis-totales")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @Operation(summary = "Obtener mis totales de ganancias (repartidor autenticado)")
    public ResponseEntity<TotalesGananciaDTO> getMisTotales(
            Authentication authentication) throws NotFoundException {

        Usuario repartidor = (Usuario) authentication.getPrincipal();
        TotalesGananciaDTO totales = gananciaService.getTotales(repartidor.getId());
        return ResponseEntity.ok(totales);
    }
}
