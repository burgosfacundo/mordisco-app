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
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;
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
     * @return Respuesta HTTP con el DTO del restaurante encontrado.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Obtener un restaurante por ID", description = "Recibe un ID y devuelve el restaurante correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @GetMapping("/{id}") // Anotación para indicar que esta función maneja las peticiones GET a la ruta /{id}
    public ResponseEntity<RestauranteResponseDTO> findById(@PathVariable Long id) throws NotFoundException {
        // Llama al servicio para obtener una promoción por su ID
        // Devuelve una respuesta HTTP 200 OK con la promoción encontrada
        return ResponseEntity.ok(restauranteService.findById(id));
    }
    /**
    * Función para obtener restaurante por su dueño.
    * @param idUsuario del usuario con rol de dueño del restaurante a buscar.
    * @return Respuesta HTTP con el DTO del restaurante encontrado.
    * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
    */
    @Operation(summary = "Obtener restaurantes por ID del dueño", description = "Recibe un ID del usuario con rol de dueño y devuelve los restaurantes correspondiente") // Anotación para documentar la operación con Swagger
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve los restaurantes correspondientes"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con rol de dueño con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    }) // Anotación para documentar las posibles respuestas
    @GetMapping("/usuario/{idUsuario}") // Anotación para indicar que esta función maneja las peticiones GET a la ruta /{idU}
    public ResponseEntity<RestauranteResponseDTO> findByUsuario(@PathVariable Long idUsuario) throws NotFoundException, BadRequestException {
    return ResponseEntity.ok(restauranteService.findByIdUsuario(idUsuario));
    }

    /**
     * Función para crear restaurante.
     * @return Respuesta HTTP con un mensaje de éxito.
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
                                       RestauranteCreateDTO dto) {
        restauranteService.save(dto);
        return ResponseEntity.ok("Restaurante guardado correctamente");
    }

    /**
     * Función para listar restaurantes.
     * @return Respuesta HTTP con una lista de DTO de restaurantes.
     */
    @Operation(summary = "Listar restaurantes", description = "Lista los restaurantes de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasAuthority('CLIENTE')")
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> findAll() {
        return ResponseEntity.ok(restauranteService.getAll());
    }

    /**
        * Función para listar restaurantes por estado.
        * @param estado Estado del restaurante (activo/inactivo) a filtrar.
        * @return Respuesta HTTP con una lista de DTO de restaurantes.
     */
    @Operation(summary = "Listar restaurantes por estado",description = "Lista los restaurantes según su estado")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado")
    public ResponseEntity<List<RestauranteResponseDTO>> getAllByEstado(@RequestParam Boolean estado) {
        List<RestauranteResponseDTO> lista = restauranteService.getAllByEstado(estado);
        return ResponseEntity.ok(lista);
    }

    /**
     * Función para listar restaurantes por ciudad.
     * @param ciudad Ciudad en la que se encuentran los restaurantes a filtrar.
     * @return Respuesta HTTP con una lista de DTO de restaurantes.
     */
    @Operation(summary = "Listar restaurantes por ciudad",description = "Lista los restaurantes según su ciudad")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ciudad")
    public ResponseEntity<List<RestauranteResponseDTO>> getAllByCiudad(@RequestParam String ciudad) {
        return ResponseEntity.ok(restauranteService.getAllByCiudad(ciudad));
    }

    /**
     * Función para listar restaurantes por nombre.
     * @param nombre Nombre del restaurante a filtrar.
     * @return Respuesta HTTP con una lista de DTO de restaurantes.
     */
    @Operation(summary = "Listar restaurantes por nombre",description = "Lista los restaurantes según su nombre")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",description = "Restaurantes listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre")
    public ResponseEntity<List<RestauranteResponseDTO>> getAllByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(restauranteService.getAllByNombre(nombre));
    }

    /**
     * Función para listar restaurantes con promociones activas.
     * @return Respuesta HTTP con una lista de DTO de restaurantes con promociones activas.
     */
    @Operation(summary = "Listar restaurantes con promociones activas", description = "Lista los restaurantes que tienen una promoción activa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurantes con promociones activas listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/promocion")
    public ResponseEntity<List<RestauranteResponseDTO>> getAllByPromocionActiva() {
        return ResponseEntity.ok(restauranteService.getAllByPromocionActiva());
    }

    /**
     * Función para actualizar un restaurante.
     *
     * @param dto DTO con los datos actualizados del restaurante.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Actualizar restaurante", description = "Recibe un DTO de restaurante y lo actualiza en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualiza el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody @Valid RestauranteUpdateDTO dto) throws NotFoundException {
        restauranteService.update(dto);
        return ResponseEntity.ok("El restaurante se actualizo exitosamente");
    }

    /**
     * Función para eliminar restaurante por su id.
     *
     * @param id del restaurante a eliminar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Eliminar restaurante por ID", description = "Recibe un id de un restaurante y lo borra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elimina el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws NotFoundException{
        restauranteService.delete(id);
        return ResponseEntity.ok("El restaurante se borro exitosamente!");
    }

    /**
     * Función para agregar o modificar horarios de atención al restaurante.
     *
     * @param id del restaurante al que se le agregarán o modificarán los horarios.
     * @param horarios Lista de horarios a agregar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Agregar horarios de atención al restaurante", description = "Recibe un ID de restaurante y una lista de horarios para agregar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horarios agregados exitosamente al restaurante"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/horarios")
    public ResponseEntity<String> addHorarios(@PathVariable Long id,
                                              @Valid @RequestBody List<HorarioAtencionDTO> horarios)
            throws NotFoundException {
        restauranteService.adHorariosAtencion(id, horarios);
        return ResponseEntity.ok("Horarios agregados exitosamente al restaurante");
    }
}
