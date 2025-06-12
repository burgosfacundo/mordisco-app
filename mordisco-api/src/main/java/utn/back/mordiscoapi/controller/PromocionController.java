package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
import utn.back.mordiscoapi.service.interf.IPromocionService;

import java.util.List;

@Tag(name = "Promociones", description = "Operaciones relacionadas con las promociones de los restaurantes") // Anotación para documentar la API con Swagger
@RestController
@RequestMapping("/api/promocion")
@RequiredArgsConstructor
public class PromocionController {
    private final IPromocionService service;

    /**
     * Función para guardar una nueva promoción.
     * @param dto Objeto DTO que contiene los datos de la promoción a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Sí hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear una promoción nueva", description = "Recibe una promoción y la guarda en la base de datos") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN') or @restauranteSecurity.puedeAccederAPropioRestaurante(#dto.restauranteId())")
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody @Valid
                                       PromocionRequestDTO dto) throws BadRequestException {
        // Llama al servicio para guardar la promoción
        service.save(dto);
        // Devuelve una respuesta HTTP 200 OK con un mensaje de éxito
        return ResponseEntity.ok().body("Promoción creada exitosamente");
    }

    /**
     * Función para obtener todas las promociones.
     * @return Respuesta HTTP con una lista de proyecciones de promociones.
     */
    @Operation(summary = "Obtener todas las promociones", description = "Devuelve una lista de todas las promociones") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve una lista de promociones"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PromocionProjection>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Función para obtener una promoción por su ID.
     * @param id de la promoción a buscar.
     * @return Respuesta HTTP con la proyección de la promoción encontrada.
     * @throws NotFoundException Si no se encuentra la promoción con el ID proporcionado.
     */
    @Operation(summary = "Obtener una promoción por ID", description = "Recibe un ID y devuelve la promoción correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve la promoción correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la promoción con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromocionProjection> findById(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Función para actualizar una promoción por su ID.
     * @param id de la promoción a actualizar.
     * @param dto Objeto DTO que contiene los nuevos datos de la promoción.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra la promoción con el ID proporcionado.
     * @throws BadRequestException Sí hay un error en los datos proporcionados.
     */
    @Operation(summary = "Actualizar una promoción", description = "Recibe un ID y una promoción y actualiza la promoción correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la promoción con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN') or @promocionSecurity.puedeAccederAPromocion(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id,
                                        @RequestBody @Valid PromocionRequestDTO dto)
            throws NotFoundException, BadRequestException {
        service.update(id,dto);
        return ResponseEntity.ok().body("Promoción actualizada exitosamente");
    }

    /**
     * Función para eliminar una promoción por su ID.
     * @param id de la promoción a eliminar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra la promoción con el ID proporcionado.
     */
    @Operation(summary = "Eliminar una promoción", description = "Recibe un ID y elimina la promoción correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la promoción con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN') or @promocionSecurity.puedeAccederAPromocion(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.ok().body("Promoción eliminada exitosamente");
    }

    @Operation(summary = "Listar promociones por restaurantes", description = "Recibe un ID y lista las promociones correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promociones listadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN') or @restauranteSecurity.puedeAccederAPropioRestaurante(#idRestaurante)")
    @GetMapping("restaurante/{idRestaurante}")
    public ResponseEntity<List<PromocionResponseDTO>> listarPromoByIdRestaurante(@PathVariable
                                                                                     Long idRestaurante) throws NotFoundException {
        return ResponseEntity.ok(service.listarPromoPorRestaurante(idRestaurante));
    }

}
