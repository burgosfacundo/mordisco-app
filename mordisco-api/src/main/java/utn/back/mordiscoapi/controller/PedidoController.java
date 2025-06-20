package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.service.interf.IPedidoService;

import java.util.List;

@Tag(name = "Pedidos", description = "Operaciones relacionadas con los pedidos de los restaurantes")
@RestController
@RequestMapping("/api/pedido")
@RequiredArgsConstructor
public class PedidoController {
    private final IPedidoService pedidoService;

    /**
     * Función para guardar un nuevo pedido.
     * @param dto Objeto DTO que contiene los datos del pedido a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si el restaurante, producto o la dirección no existen,
     */
    @Operation(summary = "Crear un pedido nuevo", description = "Recibe un pedido y lo guarda en la base de datos. " +
            "**Rol necesario: CLIENTE y puedo guardar un pedido para mi propio usuario**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Si el restaurante, producto o la dirección no existen"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE') and @usuarioSecurity.puedeAccederAUsuario(#dto.idCliente())")
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       PedidoRequestDTO dto) throws NotFoundException, BadRequestException {
        pedidoService.save(dto);
        return ResponseEntity.ok().body("Pedido creado exitosamente");
    }

    /**
     * Función para obtener todos los pedidos.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     */
    @Operation(summary = "Obtener todos los pedidos", description = "Devuelve una lista con todos los pedidos. " +
            "**Rol necesario: ADMIN**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    /**
     * Función para obtener un pedido por su ID.
     * @param id del pedido a buscar.
     * @return Respuesta HTTP con la proyección del pedido encontrado.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     */
    @Operation(summary = "Obtener un pedido por ID", description = "Recibe un ID y devuelve el pedido correspondiente. " +
            "**Rol necesario: ADMIN o ser el propietario del pedido o del restaurante asociado**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el pedido correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @pedidoSecurity.esPropietarioPedido(#id) or @pedidoSecurity.esPropietarioRestaurantePedido(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable
                                                      Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    /**
     * Función para obtener todos los pedidos de un restaurante.
     * @param id del restaurante a buscar pedidos.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     * @throws NotFoundException Si no se encuentra el restaurante con el ID proporcionado.
     */
    @Operation(summary = "Obtener todos los pedidos de un restaurante", description = "Devuelve una lista con todos los pedidos del restaurante. " +
            "**Rol necesario: ADMIN o ser el propietario del restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @restauranteSecurity.puedeAccederAPropioRestaurante(#id)")
    @GetMapping("/restaurante/{id}")
    public ResponseEntity<List<PedidoResponseDTO>> findAllByRestaurante_Id(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByRestaurante_Id(id));
    }

    /**
     * Función para eliminar un pedido por su ID.
     * @param id del pedido a eliminar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     */
    @Operation(summary = "Eliminar un pedido", description = "Recibe un ID y elimina el pedido correspondiente. " +
            "**Rol necesario: ser el propietario del pedido**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@pedidoSecurity.esPropietarioPedido(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable
                                         Long id) throws NotFoundException {
        pedidoService.delete(id);
        return ResponseEntity.ok().body("Pedido eliminado exitosamente");
    }

    /**
     * Función para modificar el estado de un pedido por su ID.
     * @param id del pedido a modificar estado.
     * @param nuevoEstado estado nuevo del pedido.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     * @throws BadRequestException Sí hay un error en los datos proporcionados.
     */
    @Operation(summary = "Modificar el estado de un pedido", description = "Recibe un ID, recibe el nuevo estado y actualiza el pedido correspondiente. " +
            "**Rol necesario: ser el propietario del restaurante del pedido**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@pedidoSecurity.esPropietarioRestaurantePedido(#id)")
    @PutMapping("/state/{id}")
    public ResponseEntity<String> changeState(
            @PathVariable
            Long id,
            @RequestParam EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        pedidoService.changeState(id,nuevoEstado);
        return ResponseEntity.ok().body("Pedido actualizado exitosamente");
    }


    /**
     * Función para cancelar el pedido por su ID.
     * @param id del pedido a cancelar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     * @throws BadRequestException Sí hay un error en los datos proporcionados.
     */
    @Operation(summary = "Cancelar el  pedido", description = "Recibe un ID y cancela el pedido correspondiente. " +
            "**Rol necesario: ser el propietario del pedido**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@pedidoSecurity.esPropietarioPedido(#id)")
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<String> cancelarPedido(
            @PathVariable
            Long id) throws NotFoundException, BadRequestException {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok().body("Pedido actualizado exitosamente");
    }



    /**
     * Función para obtener todos los pedidos de un cliente por estado.
     * @param id del cliente de los pedidos.
     * @param estado del pedido a buscar.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     */
    @Operation(summary = "Obtener todos los pedidos de un cliente por estado", description = "Devuelve una lista con todos los pedidos del cliente por estado. " +
            "**Rol necesario: ADMIN o ser el propietario del pedido**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "404", description = "No se encontró el cliente con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.puedeAccederAUsuario(#id)")
    @GetMapping("/cliente/{id}/state")
    public ResponseEntity<List<PedidoResponseDTO>> findAllByCliente_IdAndEstado(@PathVariable Long id,
                                                                          @RequestParam EstadoPedido estado) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByCliente_IdAndEstado(id, estado));
    }

    /**
     * Función para obtener todos los pedidos de un cliente.
     * @param id del cliente de los pedidos.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     */
    @Operation(summary = "Obtener todos los pedidos de un cliente", description = "Devuelve una lista con todos los pedidos del cliente. " +
            "**Rol necesario: ADMIN o ser el propietario del pedido**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.puedeAccederAUsuario(#id)")
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<PedidoResponseDTO>> findAllByCliente_Id(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByCliente_Id(id));
    }

    /**
     * Función para obtener todos los pedidos de un restaurante por estado.
     * @param id del restaurante a buscar pedidos.
     * @param estado del pedido a buscar.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     */
    @Operation(summary = "Obtener todos los pedidos de un restaurante por estado", description = "Devuelve una lista con todos los pedidos del restaurante por estado. " +
            "**Rol necesario: ADMIN o ser el propietario del restaurante**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "404", description = "No se encontró el restaurante con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ADMIN') or @restauranteSecurity.puedeAccederAPropioRestaurante(#id)")
    @GetMapping("/restaurante/{id}/state")
    public ResponseEntity<List<PedidoResponseDTO>> findAllByRestaurante_IdAndEstado(@PathVariable Long id,
                                                                                    @RequestParam EstadoPedido estado) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByRestaurante_IdAndEstado(id, estado));
    }
}

