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
import utn.back.mordiscoapi.repository.MenuRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.service.interf.IMenuService;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements IMenuService {
    private final MenuRepository menuRepository;
    private final RestauranteRepository restauranteRepository;

    @Transactional
    @Override
    public void save(Long restauranteId, String nombre) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        if (restaurante.getMenu() != null) {
            throw new BadRequestException("El restaurante ya tiene un menú asociado");
        }

        Menu menu = Menu.builder()
                .nombre(nombre)
                .build();

        menu = menuRepository.save(menu);

        restaurante.setMenu(menu);
        restauranteRepository.save(restaurante);
    }

    @Transactional
    @Override
    public MenuResponseDTO findByRestauranteId(Long restauranteId) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Menu menu = restaurante.getMenu();
        if (menu == null) {
            throw new NotFoundException("El restaurante no tiene menú asociado");
        }

        return MenuMapper.toDto(menu);
    }

    @Transactional
    @Override
    public void update(Long restauranteId, String nombre) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Menu menu = restaurante.getMenu();
        if (menu == null) {
            throw new NotFoundException("El restaurante no tiene menú");
        }

        menu.setNombre(nombre);
        menuRepository.save(menu);
    }

    @Transactional
    @Override
    public void deleteByIdRestaurante(Long restauranteId) throws NotFoundException, BadRequestException {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado"));

        Menu menu = restaurante.getMenu();
        if (menu == null) {
            throw new BadRequestException("El restaurante no tiene menú");
        }
        restaurante.setMenu(null);
        restaurante.setActivo(false);
        restauranteRepository.save(restaurante);

        menuRepository.delete(menu);
    }
}