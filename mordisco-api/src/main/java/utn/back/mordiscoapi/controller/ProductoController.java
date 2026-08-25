package utn.back.mordiscoapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseCardDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoUpdateDTO;
import utn.back.mordiscoapi.service.interf.IProductoService;


@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final IProductoService service;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('ADMIN')) or ((hasRole('RESTAURANTE')) and (@productoSecurity.puedeCrearProductoEnMenu(#dto.idMenu())))")
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody
                                     @Valid
                                     ProductoRequestDTO dto) throws BadRequestException, NotFoundException {
        service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping
    public ResponseEntity<Page<ProductoResponseCardDTO>> findAll(
            @RequestParam int page,
            @RequestParam @Valid @Positive int size,
            @Valid @Positive @RequestParam Long idMenu
    ) {
        return ResponseEntity.ok().body(service.findAllByIdMenu(page, size, idMenu));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> findById(@Valid @Positive @PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok().body(service.findById(id));
    }


    /**
     * 🆕 Obtiene los pedidos activos que contienen un producto
     */
    @Operation(
            summary = "Obtener pedidos activos que contienen un producto",
            description = "Retorna los pedidos en estado PENDIENTE, EN_PROCESO o EN_CAMINO que incluyen este producto. " +
                    "Útil para saber por qué no se puede eliminar un producto. " +
                    "**Rol necesario: RESTAURANTE (propietario) o ADMIN**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos activos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('ADMIN')) or ((hasRole('RESTAURANTE')) and (@productoSecurity.puedeAccederAProducto(#id)))")
    @GetMapping("/{id}/pedidos-activos")
    public ResponseEntity<Page<PedidoResponseDTO>> getPedidosActivos(
            @PathVariable @Valid @Positive Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws NotFoundException {

        return ResponseEntity.ok(service.getPedidosActivosByProducto(id, page, size));
    }

    /**
     * Elimina un producto por su ID
     * 🆕 Ahora valida que no esté en pedidos activos
     */
    @Operation(
            summary = "Eliminar producto por ID",
            description = """
            Elimina un producto si no está en pedidos activos.
            **Validación:**
            - No se puede eliminar si está en pedidos en estado PENDIENTE, EN_PROCESO o EN_CAMINO
            - Si la validación falla, retorna error 400 con la cantidad de pedidos activos
            **Rol necesario: RESTAURANTE (propietario) o ADMIN**
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar: está en pedidos activos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('ADMIN')) or ((hasRole('RESTAURANTE')) and (@productoSecurity.puedeAccederAProducto(#id)))")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Valid @Positive @PathVariable Long id)
            throws NotFoundException, BadRequestException {

        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("(hasRole('ADMIN')) or ((hasRole('RESTAURANTE')) and (@productoSecurity.puedeAccederAProducto(#id)))")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @Valid @Positive @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO dto
    ) throws NotFoundException {
        service.update(id,dto);
        return ResponseEntity.ok().build();
    }

}
