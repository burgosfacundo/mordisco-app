package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.MenuMapper;
import utn.back.mordiscoapi.model.dto.menu.MenuResponseDTO;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.ImagenRepository;
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.ProductoRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IMenuService;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements IMenuService {
    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final ImagenRepository imagenRepository;

    /**
     * Guarda un menú asociado a un restaurante.
     *
     * @param restauranteId ID del restaurante al que se asocia el menú.
     * @param nombre           nombre del menu
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Transactional
    @Override
    public void save(Long restauranteId, String nombre) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));
        var menu = menuRepository.save(Menu.builder()
                        .nombre(nombre)
                        .restaurante(restaurante)
                .build());
        restaurante.setMenu(menu);
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
    public MenuResponseDTO findByRestauranteId(Long restauranteId) throws NotFoundException {
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
    public void deleteByIdRestaurante(Long restauranteId) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        if (restaurante.getMenu() == null) throw new BadRequestException("El restaurante no tiene menú");

        Menu menu = restaurante.getMenu();
        restaurante.setMenu(null);
        restaurante.setActivo(false);
        restauranteRepository.save(restaurante);
        menuRepository.deleteById(menu.getId());
    }

    @Override
    public void update(Long restauranteId, String nombre) throws NotFoundException {
        var menu = menuRepository.findByRestauranteId(restauranteId).orElseThrow(
                () -> new NotFoundException("Menú no encontrado")
        );
        menu.setNombre(nombre);
        menuRepository.save(menu);
    }
}
