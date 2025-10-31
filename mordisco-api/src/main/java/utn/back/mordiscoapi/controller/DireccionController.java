package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;
import utn.back.mordiscoapi.service.impl.DireccionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/usuarios/{id}/direcciones")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService service;

    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
    @GetMapping
    public List<DireccionResponseDTO> list(@PathVariable Long id) {
        return service.list(id);
    }

    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
    @PostMapping
    public ResponseEntity<Long> create(@PathVariable Long id, @Valid @RequestBody DireccionCreateDTO dto) throws NotFoundException {
        Long createdId = service.add(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdId);
    }

    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
    @PutMapping
    public ResponseEntity<Map<String,String>> update(@PathVariable Long id,
                                         @Valid @RequestBody DireccionUpdateDTO dto) throws NotFoundException {
        service.update(id, dto);
        return ResponseEntity.ok(Map.of("message","Dirección actualizada correctamente"));
    }

    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
    @DeleteMapping("/{dirId}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long dirId) throws NotFoundException {
        service.delete(id, dirId);
        return ResponseEntity.noContent().build();
    }
}
