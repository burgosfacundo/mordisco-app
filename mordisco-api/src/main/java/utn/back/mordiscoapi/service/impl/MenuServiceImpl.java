package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.MenuMapper;
import utn.back.mordiscoapi.model.dto.menu.MenuDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.ImagenRepository;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IMenuService;

import java.util.*;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements IMenuService {
    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final ImagenRepository imagenRepository;

    /**
     * Guarda un menú asociado a un restaurante.
     * Si el menú ya existe, lo actualiza; si no, lo crea.
     * También maneja la creación o actualización de productos dentro del menú.
     *
     * @param restauranteId ID del restaurante al que se asocia el menú.
     * @param dto           DTO que contiene los datos del menú y sus productos.
     * @throws NotFoundException si el restaurante o algún producto no se encuentra.
     */
    @Transactional
    @Override
    public void save(Long restauranteId, MenuDTO dto) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Menu menu;

        if (dto.id() != null) {
            // Actualización de menú existente
            menu = menuRepository.findById(dto.id())
                    .orElseThrow(() -> new NotFoundException("Menú no encontrado"));

            // Validar que el menú pertenece al restaurante actual
           if(!restauranteRepository.existsByIdAndMenu_Id(restauranteId, dto.id())){
                throw new BadRequestException("El menú con id " + dto.id() + " no pertenece al restaurante con id " + restauranteId);
           }

            menu.setNombre(dto.nombre());
        } else {
            menu = new Menu();
            menu.setNombre(dto.nombre());
        }

        List<Producto> productos = new ArrayList<>();
        for (ProductoDTO productoDTO : dto.productos()) {
            Producto producto;

            if (productoDTO.id() != null) {
                // Editar producto existente
                producto = productoRepository.findById(productoDTO.id())
                        .orElseThrow(() -> new NotFoundException("Producto con id " + productoDTO.id() + " no encontrado"));

                // Validar que el producto pertenece al menú actual
                if (producto.getMenu() != null && !producto.getMenu().getId().equals(menu.getId())) {
                    throw new NotFoundException("El producto con id " + productoDTO.id() + " no pertenece al menú actual");
                }

                // Validar que la imagen recibida le pertenece al producto
                Long imagenDtoId = productoDTO.imagen().id();
                if (producto.getImagen() != null && imagenDtoId != null && !producto.getImagen().getId().equals(imagenDtoId)) {
                    throw new BadRequestException("La imagen con id " + imagenDtoId + " no le pertenece al producto con id " + producto.getId());
                }

                Imagen imagen;

                if(producto.getImagen() != null && imagenDtoId != null){
                    imagen = imagenRepository.findById(imagenDtoId)
                            .orElseThrow(() -> new NotFoundException("Imagen con id " + imagenDtoId + " no encontrada"));
                }else{
                    imagen = new Imagen();
                }

                imagen.setNombre(productoDTO.imagen().nombre());
                imagen.setUrl(productoDTO.imagen().url());
                producto.setImagen(imagen);
            } else {
                if (productoDTO.imagen().id() != null) {
                    throw new BadRequestException("Debes crear una nueva imagen para el producto, no puedes asignar una imagen existente");
                }
                producto = new Producto();
                producto.setImagen(Imagen.builder()
                        .nombre(productoDTO.imagen().nombre())
                        .url(productoDTO.imagen().url())
                        .build());
            }

            producto.setNombre(productoDTO.nombre());
            producto.setDescripcion(productoDTO.descripcion());
            producto.setPrecio(productoDTO.precio());
            producto.setDisponible(productoDTO.disponible());
            producto.setMenu(menu);
            productos.add(producto);
        }

        // Guardar productos
        if (menu.getProductos() == null) {
            menu.setProductos(new ArrayList<>());
        }

        menu.getProductos().clear();
        menu.getProductos().addAll(productos);

        if (dto.id() == null) {
            restaurante.setActivo(!productos.isEmpty());
            restaurante.setMenu(menu);
            restauranteRepository.save(restaurante); // guarda all: restaurante -> menú -> productos
        } else {
            menuRepository.save(menu); // actualiza menú y productos
        }
    }

    /**
     * Busca un menú por el ID del restaurante.
     *
     * @param restauranteId ID del restaurante.
     * @return DTO del menú asociado al restaurante.
     * @throws NotFoundException si el restaurante o el menú no se encuentran.
     */
    @Transactional
    @Override
    public MenuDTO findByRestauranteId(Long restauranteId) throws NotFoundException {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new NotFoundException("Restaurante no encontrado");
        }
        Menu menu = menuRepository.findByRestauranteId(restauranteId)
                .orElseThrow(() -> new NotFoundException("Menú no encontrado para el restaurante con id " + restauranteId));

        return MenuMapper.toDto(menu);
    }

    /**
     * Elimina el menú asociado a un restaurante por su ID.
     * También elimina los productos asociados al menú.
     *
     * @param restauranteId ID del restaurante cuyo menú se eliminará.
     * @throws NotFoundException si el restaurante o el menú no se encuentran.
     */
    @Override
    @Transactional
    public void deleteByIdRestaurante(Long restauranteId) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));
        Menu menu = restaurante.getMenu();
        restaurante.setMenu(null);
        restaurante.setActivo(false);
        restauranteRepository.save(restaurante);
        if (menu != null) {
            menuRepository.deleteById(menu.getId());
        }
    }
}
