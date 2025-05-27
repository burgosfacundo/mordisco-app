package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTOResponse;
import utn.back.mordiscoapi.model.dto.promocion.PromocionDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;
import utn.back.mordiscoapi.service.impl.PedidoServiceImpl;

import java.util.List;

@Tag(name = "Pedidos", description = "Operaciones relacionadas con los pedidos de los restaurantes")
@RestController
@RequestMapping("/api/pedido")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoServiceImpl pedidoService;

    /**
     * Función para guardar un nuevo pedido.
     * @param dto Objeto DTO que contiene los datos del pedido a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear un pedido nuevo", description = "Recibe un pedido y lo guarda en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       PedidoDTORequest dto) throws BadRequestException {
        pedidoService.save(dto);
        return ResponseEntity.ok().body("Pedido creado exitosamente");
    }

    /**
     * Función para obtener todos los pedidos.
     * @return Respuesta HTTP con una lista de proyecciones de pedidos.
     */
    @Operation(summary = "Obtener todos los pedidos", description = "Devuelve una lista con todos los pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedidos encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PedidoDTOResponse>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    /**
     * Función para obtener un pedido por su ID.
     * @param id del pedido a buscar.
     * @return Respuesta HTTP con la proyección del pedido encontrado.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     */
    @Operation(summary = "Obtener un pedido por ID", description = "Recibe un ID y devuelve el pedido correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el pedido correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTOResponse> findById(@PathVariable
                                                      Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    /**
     * Función para eliminar un pedido por su ID.
     * @param id del pedido a eliminar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el pedido con el ID proporcionado.
     */
    @Operation(summary = "Eliminar un pedido", description = "Recibe un ID y elimina el pedido correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Modificar el estado de un pedido", description = "Recibe un ID, recibe el nuevo estado y actualiza el pedido correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Pedido actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el pedido con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/state/{id}")
    public ResponseEntity<String> changeState(
            @RequestBody
            @Valid
            @PathVariable
            Long id,
            EstadoPedido nuevoEstado) throws NotFoundException, BadRequestException {
        pedidoService.changeState(id,nuevoEstado);
        return ResponseEntity.ok().body("Pedido actualizado exitosamente");
    }

}

