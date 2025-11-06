package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.menu.MenuResponseDTO;
import utn.back.mordiscoapi.service.interf.IMenuService;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final IMenuService menuService;

    @Operation(summary = "Crear un menú",
            description = "Crea un nuevo menú asociado a un restaurante. **Rol: RESTAURANTE**")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menú creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El restaurante ya tiene menú"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @PostMapping("/crear/{restauranteId}")
    public ResponseEntity<Void> save(
            @PathVariable Long restauranteId,
            @RequestParam @Valid @Size(max = 50) String nombre
    ) throws NotFoundException, BadRequestException {
        menuService.save(restauranteId, nombre);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtener menú por restaurante")
    @GetMapping("/{restauranteId}")
    public ResponseEntity<MenuResponseDTO> findByRestauranteId(
            @PathVariable Long restauranteId
    ) throws NotFoundException {
        return ResponseEntity.ok(menuService.findByRestauranteId(restauranteId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @PatchMapping("/{restauranteId}")
    public ResponseEntity<Void> update(
            @PathVariable Long restauranteId,
            @RequestParam @Size(max = 50) String nombre
    ) throws NotFoundException {
        menuService.update(restauranteId, nombre);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @DeleteMapping("/{restauranteId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restauranteId
    ) throws NotFoundException, BadRequestException {
        menuService.deleteByIdRestaurante(restauranteId);
        return ResponseEntity.noContent().build();
    }
}