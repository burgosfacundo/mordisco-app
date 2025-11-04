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

@RequiredArgsConstructor
@RequestMapping("/api/menus")
@RestController
public class MenuController {
    private final IMenuService menuService;

    /**
     * Endpoint para crear un nuevo menú asociado a un restaurante.
     * Solo accesible por usuarios con rol ADMIN o aquellos que tienen permiso para crear el menú del restaurante.
     *
     * @param restauranteId ID del restaurante al que se asociará el menú.
     * @param nombre           nombre del menú a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el restaurante o hay algún error al guardar el menú.
     */
    @Operation(summary = "Crear un menú", description = "Crea un nuevo menú asociado a un restaurante." +
            "**Rol necesario: RESTAURANTE y debe ser el dueño del restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Menú creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @PostMapping("/crear/{restauranteId}")
    public ResponseEntity<String> save(
            @PathVariable Long restauranteId,
            @RequestParam @Valid
            @Size(max = 50) String nombre) throws NotFoundException, BadRequestException {
        menuService.save(restauranteId, nombre);
        return ResponseEntity.status(HttpStatus.CREATED).body("Se ha creado el menu correctamente");
    }


    /**
     * Endpoint para obtener el menú asociado a un restaurante específico.
     * Solo accesible por usuarios con rol ADMIN o aquellos que tienen permiso para ver el menú del restaurante.
     *
     * @param restauranteId ID del restaurante cuyo menú se desea obtener.
     * @return Respuesta HTTP con el menú encontrado.
     * @throws NotFoundException Si no se encuentra el menú asociado al restaurante.
     */
    @Operation(summary = "Obtener menú por ID de restaurante", description = "Obtiene el menú asociado a un restaurante específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Menú encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Menú no encontrado para el restaurante"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{restauranteId}")
    public ResponseEntity<MenuResponseDTO> findByRestauranteId(@PathVariable Long restauranteId) throws NotFoundException {
        return ResponseEntity.ok(menuService.findByRestauranteId(restauranteId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @PatchMapping("/{restauranteId}")
    public ResponseEntity<String> update(
            @PathVariable Long restauranteId,
            @RequestParam String nombre
    ) throws NotFoundException {
        menuService.update(restauranteId,nombre);
        return ResponseEntity.ok("Se modifico el menu correctamente");
    }



    /**
     * Endpoint para borrar el menú asociado a un restaurante específico.
     * Solo accesible por usuarios con rol ADMIN o aquellos que tienen permiso para modificar el menú del restaurante.
     *
     * @param restauranteId ID del restaurante cuyo menú se desea borrar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el menú asociado al restaurante.
     */
    @Operation(summary = "Borrar menú", description = "Borra el menú asociado a un restaurante específico. " +
            "**Rol necesario: RESTAURANTE y debe ser el dueño del restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Menú borrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "404", description = "Menú no encontrado para el restaurante"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#restauranteId)")
    @DeleteMapping("/{restauranteId}")
    public ResponseEntity<String> deleteByRestauranteId(@PathVariable Long restauranteId) throws NotFoundException, BadRequestException {
        menuService.deleteByIdRestaurante(restauranteId);
        return ResponseEntity.noContent().build();
    }
}
