package utn.back.mordiscoapi.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
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
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       ProductoRequestDTO dto) throws BadRequestException, NotFoundException {
        service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Producto creado exitosamente");
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


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@Valid @Positive @PathVariable Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Producto eliminado exitosamente");
    }


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> update(
            @Valid @Positive @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO dto
    ) throws NotFoundException {
        service.update(id,dto);
        return ResponseEntity.ok().body("Producto actualizado exitosamente");
    }

}
