package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.service.interf.IHorarioService;

@Tag(name = "Promociones", description = "Operaciones relacionadas con las promociones de los restaurantes") // Anotación para documentar la API con Swagger
@RestController
@RequestMapping("/api/restaurantes/horarios")
@RequiredArgsConstructor
public class HorarioController {
    private final IHorarioService horarioService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#idRestaurante)) or @PreAuthorize(hasRole('ADMIN')")
    @PostMapping("/{idRestaurante}")
    public ResponseEntity<String> save(
            @Valid @Positive @PathVariable Long idRestaurante,
            @RequestBody @Valid HorarioAtencionRequestDTO dto
    )
            throws NotFoundException{
        horarioService.save(idRestaurante, dto);
        return ResponseEntity.ok("Horario agregado exitosamente al restaurante");
    }

    @GetMapping("/{idRestaurante}")
    public ResponseEntity<Page<HorarioAtencionResponseDTO>> getAllByIdRestaurante(
            @RequestParam @Valid int page, @RequestParam @Valid @Positive int size,
            @Valid @Positive @PathVariable Long idRestaurante
    ) throws NotFoundException {
        return ResponseEntity.ok().body(horarioService.findAllByIdRestaurante(page,size,idRestaurante));
    }


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') or @PreAuthorize(hasRole('ADMIN')")
    @PutMapping("/{idHorario}")
    public ResponseEntity<String> update(
            @Valid @Positive @PathVariable Long idHorario,
            @Valid @RequestBody HorarioAtencionRequestDTO dto
    ) throws NotFoundException {
        horarioService.update(idHorario,dto);
        return ResponseEntity.ok().body("Horario actualizado correctamente");
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#id) or @PreAuthorize(hasRole('ADMIN'))")
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<String> delete(
            @Valid @Positive @PathVariable Long idHorario
    ) throws NotFoundException {
        horarioService.delete(idHorario);
        return ResponseEntity.ok().body("Horario eliminado correctamente");
    }
}
