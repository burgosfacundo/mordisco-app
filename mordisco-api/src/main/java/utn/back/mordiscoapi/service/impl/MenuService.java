package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.MenuMapper;
import utn.back.mordiscoapi.model.dto.menu.MenuDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public void save(Long restauranteId, MenuDTO dto) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Menu menu;

        if (dto.id() != null) {
            // Actualización de menú existente
            menu = menuRepository.findById(dto.id())
                    .orElseThrow(() -> new NotFoundException("Menú no encontrado"));
            menu.setNombre(dto.nombre());
        } else {
            // Creación de menú nuevo
            menu = new Menu();
            menu.setNombre(dto.nombre());
        }

        List<Producto> productos = new ArrayList<>();
        for (ProductoRequestDTO productoRequestDTO : dto.productos()) {
            Producto producto;

            if (productoRequestDTO.id() != null) {
                // Editar producto existente
                producto = productoRepository.findById(productoRequestDTO.id())
                        .orElseThrow(() -> new NotFoundException("Producto con id " + productoRequestDTO.id() + " no encontrado"));
            } else {
                // Nuevo producto
                producto = new Producto();
            }

            producto.setNombre(productoRequestDTO.nombre());
            producto.setDescripcion(productoRequestDTO.descripcion());
            producto.setPrecio(productoRequestDTO.precio());
            producto.setDisponible(productoRequestDTO.disponible());
            producto.setMenu(menu);
            producto.setImagen(Imagen.builder()
                            .id(productoRequestDTO.imagen().id())
                            .url(productoRequestDTO.imagen().url())
                            .nombre(productoRequestDTO.imagen().nombre())
                    .build());
            productos.add(producto);
        }

        menu.getProductos().clear();
        menu.getProductos().addAll(productos);
        if (dto.id() == null) {
            restaurante.setMenu(menu);
            restauranteRepository.save(restaurante); // guarda all: restaurante -> menú -> productos
        } else {
            menuRepository.save(menu); // actualiza menú y productos
        }
    }

    @Transactional
    public MenuDTO findByRestauranteId(Long restauranteId) throws NotFoundException {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new NotFoundException("Restaurante no encontrado");
        }
        Menu menu = menuRepository.findByRestauranteId(restauranteId)
                .orElseThrow(() -> new NotFoundException("Menú no encontrado para el restaurante con id " + restauranteId));

        return MenuMapper.toDto(menu);
    }

    @Transactional
    public void deleteByIdRestaurante(Long restauranteId) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));
        Menu menu = restaurante.getMenu();
        restaurante.setMenu(null);
        restauranteRepository.save(restaurante);
        if (menu != null) {
            menuRepository.deleteById(menu.getId());
        }
    }
}
