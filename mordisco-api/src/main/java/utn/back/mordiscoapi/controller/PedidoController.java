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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pago.MercadoPagoPreferenceResponse;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.BajaLogicaRequestDTO;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.service.interf.IPedidoService;


@Tag(name = "Pedidos", description = "Operaciones relacionadas con los pedidos de los restaurantes")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final IPedidoService pedidoService;

    /**
     * Función para guardar un nuevo pedido
     * @param dto Objeto DTO que contiene los datos del pedido a crear
     * @return MercadoPagoPreferenceResponse si es Mercado Pago, o respuesta vacía si es efectivo
     * @throws NotFoundException Si el restaurante, producto o la dirección no existen
     * @throws BadRequestException Si hay errores de validación
     */
    @Operation(summary = "Crear un pedido nuevo",
            description = "Recibe un pedido y lo guarda en la base de datos. " +
                    "Si el método de pago es Mercado Pago, retorna la URL de pago. " +
                    "**Rol necesario: CLIENTE**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Restaurante, producto o dirección no encontrados"),
            @ApiResponse(responseCode = "400", description = "Error en validación de datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENTE') and @usuarioSecurity.puedeAccederAUsuario(#dto.idCliente())")
    @PostMapping("/save")
    public ResponseEntity<MercadoPagoPreferenceResponse> save(
            @RequestBody @Valid PedidoRequestDTO dto) throws NotFoundException, BadRequestException {
        MercadoPagoPreferenceResponse response = pedidoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<Page<PedidoResponseDTO>> findAll(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(pedidoService.findAll(page, size));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('REPARTIDOR') or @pedidoSecurity.esPropietarioPedido(#id) or @pedidoSecurity.esPropietarioRestaurantePedido(#id)")
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
    @GetMapping("/restaurantes/{id}")
    public ResponseEntity<Page<PedidoResponseDTO>> findAllByRestaurante_Id(
            @PathVariable Long id,
            @RequestParam int page,
            @RequestParam int size) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByRestaurante_Id(page,size,id));
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
            @ApiResponse(responseCode = "204",description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@pedidoSecurity.esPropietarioPedido(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable
                                       Long id) throws NotFoundException {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> changeState(
            @PathVariable
            Long id,
            @RequestParam EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        pedidoService.changeState(id,nuevoEstado);
        return ResponseEntity.ok().build();
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
    @PreAuthorize("hasRole('CLIENTE') or hasRole('RESTAURANTE')")    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarPedido(
            @PathVariable
            Long id) throws NotFoundException, BadRequestException {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok().build();
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
    @GetMapping("/clientes/{id}/state")
    public ResponseEntity<Page<PedidoResponseDTO>> findAllByCliente_IdAndEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado,
            @RequestParam int page,
            @RequestParam int size) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByCliente_IdAndEstado(page,size,id, estado));
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
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Page<PedidoResponseDTO>> findAllByCliente_Id(
            @PathVariable Long id,
            @RequestParam int page,
            @RequestParam int size) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByCliente_Id(page,size,id));
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
    @GetMapping("/restaurantes/{id}/state")
    public ResponseEntity<Page<PedidoResponseDTO>> findAllByRestaurante_IdAndEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado,
            @RequestParam int page,
            @RequestParam int size) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findAllByRestaurante_IdAndEstado(page,size,id, estado));
    }


    /**
     * 🆕 Obtener pedidos disponibles para repartidor
     */
    @Operation(
            summary = "Obtener pedidos disponibles para repartidor",
            description = """
        Retorna pedidos en estado EN_CAMINO sin repartidor asignado,
        filtrados por proximidad a la ubicación del repartidor.
        **Rol necesario: REPARTIDOR**
    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @GetMapping("/disponibles-repartidor")
    public ResponseEntity<Page<PedidoResponseDTO>> getPedidosDisponibles(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                pedidoService.getPedidosDisponiblesParaRepartidor(latitud, longitud, page, size)
        );
    }


    @Operation(
            summary = "Aceptar pedido como repartidor",
            description = """
        Permite a un repartidor aceptar un pedido disponible.
        **Validaciones:**
        - El pedido debe estar EN_CAMINO
        - No debe tener repartidor asignado
        - El repartidor debe estar APROBADO
        **Rol necesario: REPARTIDOR**
    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @PostMapping("/{pedidoId}/aceptar-repartidor")
    public ResponseEntity<Void> aceptarPedido(
            @PathVariable Long pedidoId,
            Authentication authentication) throws NotFoundException, BadRequestException {

        Usuario repartidor = (Usuario) authentication.getPrincipal();
        pedidoService.asignarRepartidor(pedidoId, repartidor.getId());

        return ResponseEntity.ok().build();
    }

    /**
     * 🆕 Marcar pedido como entregado
     */
    @Operation(
            summary = "Marcar pedido como entregado",
            description = """
        Permite al repartidor confirmar que entregó el pedido.
        **Validaciones:**
        - El pedido debe estar asignado al repartidor
        - El pedido debe estar EN_CAMINO
        **Rol necesario: REPARTIDOR**
    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @PutMapping("/{pedidoId}/entregar")
    public ResponseEntity<Void> marcarComoEntregado(
            @PathVariable Long pedidoId,
            Authentication authentication) throws NotFoundException, BadRequestException {

        Usuario repartidor = (Usuario) authentication.getPrincipal();
        pedidoService.marcarComoEntregado(pedidoId, repartidor.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Obtener pedido activo del repartidor",
            description = "Retorna el pedido que el repartidor tiene actualmente asignado y en curso"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @GetMapping("/mi-pedido-activo")
    public ResponseEntity<PedidoResponseDTO> getMiPedidoActivo(Authentication authentication)
            throws NotFoundException, BadRequestException {

        Usuario repartidor = (Usuario) authentication.getPrincipal();
        PedidoResponseDTO pedido = pedidoService.getPedidoActivoRepartidor(repartidor.getId());

        return ResponseEntity.ok(pedido);
    }

    /**
     * Cancela un pedido con motivo obligatorio
     * Solo accesible por administradores
     */
    @Operation(
            summary = "Cancela un pedido con motivo obligatorio",
            description = """
            Cancela un pedido especificando un motivo.
            **Rol necesario: ADMIN**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(
            @PathVariable Long id,
            @RequestBody @Valid BajaLogicaRequestDTO dto)
            throws NotFoundException {
        pedidoService.darDeBaja(id, dto.motivo());
        return ResponseEntity.ok().build();
    }

    /**
     * Anula cancelacion de un pedido con motivo obligatorio
     * Solo accesible por administradores
     */
    @Operation(
            summary = "Anula cancelacion de un pedido",
            description = """
            Anula cancelacion de un pedido si fue cancelado por un administrador.
            **Rol necesario: ADMIN**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido activo exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) throws NotFoundException {
        pedidoService.reactivar(id);
        return ResponseEntity.ok().build();
    }
}

