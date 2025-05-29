package utn.back.mordiscoapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.menu.MenuDTO;
import utn.back.mordiscoapi.service.impl.MenuService;

@RequiredArgsConstructor
@RequestMapping("/api/menu")
@RestController
public class MenuController {
    private final MenuService menuService;

    @PostMapping("/crear/{restauranteId}")
    public ResponseEntity<String> save(@PathVariable Long restauranteId, @RequestBody @Valid MenuDTO dto) throws NotFoundException {
        menuService.save(restauranteId, dto);
        return ResponseEntity.ok("Se ha creado el menu correctamente");
    }

    @GetMapping("/{restauranteId}")
    public ResponseEntity<MenuDTO> findByRestauranteId(@PathVariable Long restauranteId) throws NotFoundException {
        return ResponseEntity.ok(menuService.findByRestauranteId(restauranteId));
    }

    @DeleteMapping("/{restauranteId}")
    public ResponseEntity<String> deleteByRestauranteId(@PathVariable Long restauranteId) throws NotFoundException {
        menuService.deleteByIdRestaurante(restauranteId);
        return ResponseEntity.ok("Se ha eliminado el menu correctamente");
    }
}
