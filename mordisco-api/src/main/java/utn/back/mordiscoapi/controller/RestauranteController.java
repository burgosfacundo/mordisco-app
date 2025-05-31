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
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseListarDTO;
import utn.back.mordiscoapi.service.impl.RestauranteServiceImpl;

import java.util.List;

@Tag(name = "Restaurante", description = "Operaciones relacionadas con los restaurantes")
@RestController
@RequestMapping("/api/restaurante")
@RequiredArgsConstructor
public class RestauranteController {
    private final RestauranteServiceImpl restauranteService;


    /**
     * Función para obtener un restaurante por su ID.
     * @param id del restaurante a buscar.
     * @return Respuesta HTTP con la proyección del restaurante encontrado.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Obtener un restaurante por ID", description = "Recibe un ID y devuelve el restaurante correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @GetMapping("/buscar/{id}") // Anotación para indicar que esta función maneja las peticiones GET a la ruta /{id}
    public ResponseEntity<RestauranteResponseListarDTO> findById(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                                        Long id) throws NotFoundException {
        // Llama al servicio para obtener una promoción por su ID
        // Devuelve una respuesta HTTP 200 OK con la promoción encontrada
        return ResponseEntity.ok(restauranteService.findById(id));
    }
    /**
    * Función para obtener restaurante por su duenio.
    * @param idUsuario del duenio de los restaurantes a buscar.
    * @return Respuesta HTTP con la proyección de los restaurante encontrado.
    * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
    */
    @Operation(summary = "Obtener restaurantes por ID del duenio", description = "Recibe un ID del duenio y devuelve los restaurantes correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve los restaurantes correspondientes"),
            @ApiResponse(responseCode = "404", description = "No se encontró el duenio con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @GetMapping("/usuario/{idUsuario}") // Anotación para indicar que esta función maneja las peticiones GET a la ruta /{idU}
    public ResponseEntity<RestauranteResponseDTO> findByDuenio(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                            Long idUsuario) throws NotFoundException, BadRequestException {
    return ResponseEntity.ok(restauranteService.findProjectByDuenio(idUsuario));
}
    /**
     * Función para eliminar restaurante por su id.
     *
     * @param id del restaurante  a eliminar.
     * @return Respuesta HTTP con la proyección de los restaurante eliminado.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Eliminar restaurante por ID", description = "Recibe un id de un restaurante y lo borra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elimina el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/delete/{id:[0-9]+}")
    public ResponseEntity<String> delete(@PathVariable // Anotación para indicar que este parámetro se obtiene de la ruta
                                                          Long id) throws NotFoundException{
        restauranteService.delete(id);
        return ResponseEntity.ok("El restaurante se borro exitosamente!");
    }
    /**
     * Función para crear restaurante.
     * @return Respuesta HTTP con la proyección de los restaurante creado con exito.
     * @throws NotFoundException Si ocurre un error a la hora de crear un restaurante.
     */
    @Operation(summary = "Crear un restaurante nuevo", description = "Recibe un restaurante y lo guarda en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       RestauranteDTO dto) throws BadRequestException {
        restauranteService.save(dto);
        return ResponseEntity.ok("Restaurante guardado correctamente");
    }

    /**
     * Función para listar restaurantes.
     * @return Respuesta HTTP con la proyección de los restaurante eliminado.
     * @throws BadRequestException, NotFoundException Si ocurre un error.
     */
    @Operation(summary = "Listar restaurantes", description = "Lista los restaurantes de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listarRestaurantes")
    public ResponseEntity<List<RestauranteResponseListarDTO>>  listarTodos () throws BadRequestException, NotFoundException {
        return ResponseEntity.ok(restauranteService.listarTodos());
    }
    /**
        * Función para listar restaurantes.
        * @return Respuesta HTTP con la proyección de los restaurante eliminado.
         * @throws NotFoundException Si ocurre un error.
     */
    @Operation(summary = "Listar restaurantes por estado",description = "Lista los restaurantes segun su estado")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listarByEstado/{estadoRestaurante}")
    public ResponseEntity<List<RestauranteResponseListarDTO>> listarPorEstado(
            @PathVariable Boolean estadoRestaurante) throws NotFoundException {
        List<RestauranteResponseListarDTO> lista = restauranteService.listarPorEstado(estadoRestaurante);
        return ResponseEntity.ok(lista);
    }
    @Operation(summary = "Listar restaurantes por ciudad",description = "Lista los restaurantes segun su ciudad")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listarPorCiudad/{ciudad}")
    public ResponseEntity<List<RestauranteResponseListarDTO>> listarPorCiudad(@PathVariable
                                                                        String ciudad) throws NotFoundException {
        List<RestauranteResponseListarDTO> lista = restauranteService.listarPorCiudad(ciudad);
        return ResponseEntity.ok(lista);
    }
}
