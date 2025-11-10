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
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;
import utn.back.mordiscoapi.service.interf.ICalificacionRestaurante;


@Tag(name = "CalificacionRestaurante", description = "Operaciones relacionadas con las calificaciones de los restaurantes")
@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionRestauranteController {
    private final ICalificacionRestaurante service;

    /**
     * Función para guardar una nueva calificación restaurante.
     * @param dto Objeto DTO que contiene los datos de la calificación del restaurante a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Sí hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear una calificación restaurante nueva",
            description = "Recibe una calificación restaurante y la guarda en la base de datos. " +
                    "**Rol necesario: CLIENTE y debe haber realizado un pedido en el restaurante.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Calificación restaurante creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "404", description = "Id no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE') and @calificacionSecurity.puedeAccederADtoConCalificacion(#dto)")
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody
                                     @Valid
                                     CalificacionRestauranteDTO dto) throws BadRequestException, NotFoundException {
        service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Función para obtener todas las calificaciones de restaurantes.
     * @return Respuesta HTTP con una lista de proyecciones de calificaciones de restaurante.
     */
    @Operation(summary = "Obtener todas las promociones",
            description = "Devuelve una lista de todas las calificaciones de restaurante. " +
                    "**Rol necesario: ADMIN**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve una lista de promociones"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CalificacionRestauranteProjection>> findAll(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(service.findAll(page,size));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#idRestaurante))")
    @GetMapping("/{idRestaurante}")
    public ResponseEntity<Page<CalificacionRestauranteProjection>> findAllByIdRestaurante(
            @RequestParam int page,
            @RequestParam int size,
            @PathVariable Long idRestaurante
    ) {
        return ResponseEntity.ok(service.findAllByIdRestaurante(page,size,idRestaurante));
    }


    /**
     * Función para borrar una calificación de restaurante por su ID.
     * @param id ID de la calificación que se desea borrar.
     * @throws NotFoundException Si la calificación con el ID proporcionado no existe.
     */
    @Operation(summary = "Borrar una calificación",
            description = "Borra la calificación que tenga el id que se le pasa. " +
                    "**Rol necesario: CLIENTE y debe ser el autor de la calificación.**")
    @ApiResponses(value ={
            @ApiResponse(responseCode =  "204", description = "Elimina la calificación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "404", description = "Calificación no encontrada")})
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE') and @calificacionSecurity.esAutorDeCalificacion(#id)")
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable
                                           Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
