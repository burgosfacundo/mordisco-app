package utn.back.mordiscoapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.direccion.DireccionRequestDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.service.interf.IDireccionService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class DireccionController {

    private final IDireccionService direccionService;

    @GetMapping("/me/direcciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'RESTAURANTE')")
    public ResponseEntity<List<DireccionResponseDTO>> getMisDirecciones()
            throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(direccionService.getMisDirecciones());
    }

    @PostMapping("/me/direcciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'RESTAURANTE')")
    public ResponseEntity<DireccionResponseDTO> createMiDireccion(
            @Valid @RequestBody DireccionRequestDTO dto)
            throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(direccionService.createMiDireccion(dto));
    }

    @PutMapping("/me/direcciones/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'RESTAURANTE')")
    public ResponseEntity<DireccionResponseDTO> updateMiDireccion(
            @PathVariable Long id,
            @Valid @RequestBody DireccionRequestDTO dto)
            throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(direccionService.updateMiDireccion(id, dto));
    }

    @DeleteMapping("/me/direcciones/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'RESTAURANTE')")
    public ResponseEntity<Void> deleteMiDireccion(@PathVariable Long id)
            throws NotFoundException, BadRequestException {
        direccionService.deleteMiDireccion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/direcciones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DireccionResponseDTO>> getDireccionesByUsuario(
            @PathVariable Long userId) throws NotFoundException {
        return ResponseEntity.ok(direccionService.getByUsuarioId(userId));
    }
}