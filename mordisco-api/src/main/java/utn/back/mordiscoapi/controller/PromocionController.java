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
import utn.back.mordiscoapi.model.dto.PromocionDTO;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
import utn.back.mordiscoapi.service.impl.PromocionServiceImpl;

import java.util.List;

@Tag(name = "Promociones", description = "Operaciones relacionadas con las promociones de los restaurantes") // Anotación para documentar la API con Swagger
@RestController // Anotación para indicar que esta clase es un controlador REST
@RequestMapping("/api/promocion") // Ruta base para las peticiones a este controlador
@RequiredArgsConstructor // Anotación de lombok para generar un constructor con los campos finales
public class PromocionController {
    // Inyección de dependencias de PromocionService a través del constructor de lombok @RequiredArgsConstructor
    private final PromocionServiceImpl service;

    /**
     * Función para guardar una nueva promoción.
     * @param dto Objeto DTO que contiene los datos de la promoción a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear una promoción nueva", description = "Recibe una promoción y la guarda en la base de datos") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @PostMapping("/save") // Anotación para indicar que esta función maneja las peticiones POST a la ruta /save
    public ResponseEntity<String> save(@RequestBody // Anotación para indicar que el cuerpo de la petición(JSON) se mapea a este parámetro
                                       @Valid // Anotación para validar el objeto DTO según las restricciones definidas en la clase DTO
                                       PromocionDTO dto) throws BadRequestException {
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
    }) // Anotación para documentar las posibles respuestas
    @GetMapping // Anotación para indicar que esta función maneja las peticiones GET a la ruta base
    public ResponseEntity<List<PromocionProjection>> findAll() {
        // Llama al servicio para obtener todas las promociones
        // Devuelve una respuesta HTTP 200 OK con la lista de promociones
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
    }) // Anotación para documentar las posibles respuestas
    @GetMapping("/{id}") // Anotación para indicar que esta función maneja las peticiones GET a la ruta /{id}
    public ResponseEntity<PromocionProjection> findById(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                                        Long id) throws NotFoundException {
        // Llama al servicio para obtener una promoción por su ID
        // Devuelve una respuesta HTTP 200 OK con la promoción encontrada
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Función para actualizar una promoción por su ID.
     * @param id de la promoción a actualizar.
     * @param dto Objeto DTO que contiene los nuevos datos de la promoción.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra la promoción con el ID proporcionado.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Actualizar una promoción", description = "Recibe un ID y una promoción y actualiza la promoción correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la promoción con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @PutMapping("/{id}") // Anotación para indicar que esta función maneja las peticiones PUT a la ruta /update
    public ResponseEntity<String> update(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                         Long id,
                                        @RequestBody // Anotación para indicar que el cuerpo de la petición(JSON) se mapea a este parámetro
                                        @Valid // Anotación para validar el objeto DTO según las restricciones definidas en la clase DTO
                                        PromocionDTO dto) throws NotFoundException, BadRequestException {
        // Llama al servicio para actualizar la promoción
        service.update(id,dto);
        // Devuelve una respuesta HTTP 200 OK con un mensaje de éxito
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
    }) // Anotación para documentar las posibles respuestas
    @DeleteMapping("/{id}") // Anotación para indicar que esta función maneja las peticiones DELETE a la ruta /{id}
    public ResponseEntity<String> delete(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                         Long id) throws NotFoundException {
        // Llama al servicio para eliminar la promoción
        service.delete(id);
        // Devuelve una respuesta HTTP 200 OK con un mensaje de éxito
        return ResponseEntity.ok().body("Promoción eliminada exitosamente");
    }
}
