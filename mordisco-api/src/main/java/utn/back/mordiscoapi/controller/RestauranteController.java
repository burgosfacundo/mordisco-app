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
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;
import utn.back.mordiscoapi.service.interf.IRestauranteService;

@Tag(name = "Restaurante", description = "Operaciones relacionadas con los restaurantes")
@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {
    private final IRestauranteService restauranteService;
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
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> findById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(restauranteService.findById(id));
    }
    /**
    * Función para obtener restaurante por su dueño.
    * @param idUsuario del usuario con rol de dueño del restaurante a buscar.
    * @return Respuesta HTTP con el DTO del restaurante encontrado.
    * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
    */
    @Operation(summary = "Obtener restaurantes por ID del dueño",
            description = "Recibe un ID del usuario con rol de dueño y devuelve el restaurante correspondiente a ese usuario." +
                    "**Rol necesario: ADMIN o El propio dueño del restaurante puede acceder a su restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve los restaurantes correspondientes"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con rol de dueño con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or (@usuarioSecurity.puedeAccederAUsuario(#idUsuario) and hasRole('RESTAURANTE'))")
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<RestauranteResponseDTO> findByUsuario(@PathVariable Long idUsuario) throws NotFoundException, BadRequestException {
    return ResponseEntity.ok(restauranteService.findByIdUsuario(idUsuario));
    }

    /**
     * Función para crear restaurante.
     * @return Respuesta HTTP con un mensaje de éxito.
     */
    @Operation(summary = "Crear un restaurante nuevo", description = "Recibe un restaurante y lo guarda en la base de datos. " +
            "**El dueño del restaurante puede crear su propio restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @usuarioSecurity.puedeAccederAUsuario(#dto.idUsuario())")
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody
                                       @Valid
                                       RestauranteCreateDTO dto) {
        restauranteService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    @GetMapping
    public ResponseEntity<Page<RestauranteResponseDTO>> findAll(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(restauranteService.getAll(page,size));
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
    public ResponseEntity<Page<RestauranteResponseDTO>> getAllByEstado(
            @RequestParam Boolean estado,
            @RequestParam int page,
            @RequestParam int size) {
        Page<RestauranteResponseDTO> lista = restauranteService.getAllByEstado(page,size,estado);
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
    @GetMapping("/ciudades")
    public ResponseEntity<Page<RestauranteResponseCardDTO>> getAllByCiudad(
            @RequestParam String ciudad,
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(restauranteService.getAllByCiudad(page,size,ciudad));
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
    public ResponseEntity<Page<RestauranteResponseCardDTO>> getAllByNombre(
            @RequestParam String nombre,
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok().body(restauranteService.getAllByNombre(page,size,nombre));
    }

    /**
     * Función para listar restaurantes con promociones activas.
     * @return Respuesta HTTP con una lista de DTO de restaurantes con promociones activas.
     */
    @Operation(summary = "Listar restaurantes en una ciudad con promociones activas", description = "Lista los restaurantes en una ciudad que tienen una promoción activa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurantes con promociones activas listados exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/promociones")
    public ResponseEntity<Page<RestauranteResponseCardDTO>> findAllWithPromocionActivaAndCiudad(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String ciudad) {
        return ResponseEntity.ok(restauranteService.findAllWithPromocionActivaAndCiudad(page,size,ciudad));
    }

    /**
     * Función para actualizar un restaurante.
     *
     * @param dto DTO con los datos actualizados del restaurante.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     */
    @Operation(summary = "Actualizar restaurante", description = "Recibe un DTO de restaurante y lo actualiza en la base de datos. " +
            "**El propio dueño del restaurante puede acceder a su restaurante para actualizarlo**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualiza el restaurante correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#dto.id)")
    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody @Valid RestauranteUpdateDTO dto) throws NotFoundException, BadRequestException {
        restauranteService.update(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 🆕 Obtiene los pedidos activos de un restaurante
     */
    @Operation(
            summary = "Obtener pedidos activos de un restaurante",
            description = "Retorna los pedidos en estado PENDIENTE, EN_PROCESO o EN_CAMINO. " +
                    "Útil para saber por qué no se puede eliminar un restaurante. " +
                    "**Rol necesario: ADMIN o propietario del restaurante**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos activos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @restauranteSecurity.puedeAccederAPropioRestaurante(#id)")
    @GetMapping("/{id}/pedidos-activos")
    public ResponseEntity<Page<PedidoResponseDTO>> getPedidosActivos(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws NotFoundException {

        return ResponseEntity.ok(restauranteService.getPedidosActivos(id, page, size));
    }

    /**
     * Función para eliminar restaurante por su id.
     */
    @Operation(
            summary = "Eliminar restaurante por ID",
            description = """
            Elimina un restaurante si no tiene pedidos activos.
            **Validación:**
            - No se puede eliminar si tiene pedidos en estado PENDIENTE, EN_PROCESO o EN_CAMINO
            - Si la validación falla, retorna error 400 con la cantidad de pedidos activos
            **Rol necesario: RESTAURANTE (propietario) o ADMIN**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurante eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar: tiene pedidos activos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') and @restauranteSecurity.puedeAccederAPropioRestaurante(#id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
            throws NotFoundException, BadRequestException {

        restauranteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

