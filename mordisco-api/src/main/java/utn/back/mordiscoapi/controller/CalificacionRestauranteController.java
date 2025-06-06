package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;
import utn.back.mordiscoapi.service.impl.CalificacionRestauranteImpl;

import java.util.List;

@Tag(name = "CalificacionRestaurante", description = "Operaciones relacionadas con las calificaciones de los restaurantes")
@RestController
@RequestMapping("/api/calificacion")
@RequiredArgsConstructor
public class CalificacionRestauranteController {
    private final CalificacionRestauranteImpl service;

    /**
     * Función para guardar una nueva calificacion restaurante.
     * @param dto Objeto DTO que contiene los datos de la calificacion del restaurante a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear una calificación restaurante nueva", description = "Recibe una calificacion restaurante y la guarda en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Calificación restaurante creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "404", description = "Id no encontrado")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       CalificacionRestauranteDTO dto) throws BadRequestException, NotFoundException {
        service.save(dto);
        return ResponseEntity.ok("Calificación restaurante creada exitosamente");
    }

    /**
     * Función para obtener todas las calificaciones de restaurantes.
     * @return Respuesta HTTP con una lista de proyecciones de calificaciones de restaurante.
     */
    @Operation(summary = "Obtener todas las promociones", description = "Devuelve una lista de todas las calificaciones de restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve una lista de promociones"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @GetMapping
    public ResponseEntity<List<CalificacionRestauranteProjection>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Borrar una calificación", description = "Borra la calificación que tenga el id que se le pasa")
    @ApiResponses(value ={
            @ApiResponse(responseCode =  "200", description = "Elimina la calificación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "404", description = "Calificación no encontrada")})
    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable
                                             Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.ok("La calificación fue eliminada correctamente!");
    }



}
