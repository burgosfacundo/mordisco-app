package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.menu.MenuDTO;

public interface IMenuService {
    void save(Long restauranteId, MenuDTO dto) throws NotFoundException, BadRequestException;
    MenuDTO findByRestauranteId(Long restauranteId) throws NotFoundException;
    void deleteByIdRestaurante(Long restauranteId) throws NotFoundException;
}
