package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;
import utn.back.mordiscoapi.service.interf.IHorarioService;

import java.util.Map;

@Tag(name = "Promociones", description = "Operaciones relacionadas con las promociones de los restaurantes") // Anotación para documentar la API con Swagger
@RestController
@RequestMapping("/api/restaurantes/horarios")
@RequiredArgsConstructor
public class HorarioController {
    private final IHorarioService horarioService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#idRestaurante)")
    @PostMapping("/{idRestaurante}")
    public ResponseEntity<Long> save(
            @Valid @Positive @PathVariable Long idRestaurante,
            @RequestBody @Valid HorarioAtencionRequestDTO dto
    )
            throws NotFoundException{
        Long createdId=horarioService.save(idRestaurante, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdId);
    }

    @GetMapping("/{idRestaurante}")
    public ResponseEntity<Page<HorarioAtencionResponseDTO>> getAllByIdRestaurante(
            @RequestParam @Valid int page, @RequestParam @Valid @Positive int size,
            @Valid @Positive @PathVariable Long idRestaurante
    ) throws NotFoundException {
        return ResponseEntity.ok().body(horarioService.findAllByIdRestaurante(page,size,idRestaurante));
    }


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @PutMapping("/{idHorario}")
    public ResponseEntity<Map<String,String>> update(
            @Valid @Positive @PathVariable Long idHorario,
            @Valid @RequestBody HorarioAtencionRequestDTO dto
    ) throws NotFoundException {
        horarioService.update(idHorario,dto);
        return ResponseEntity.ok(Map.of("message","Horario actualizado correctamente"));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<Void> delete(
            @Valid @Positive @PathVariable Long idHorario
    ) throws NotFoundException {
        horarioService.delete(idHorario);
        return ResponseEntity.noContent().build();
    }
}
