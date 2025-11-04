package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.menu.MenuResponseDTO;

public interface IMenuService {
    void save(Long restauranteId, String dto) throws NotFoundException, BadRequestException;
    MenuResponseDTO findByRestauranteId(Long restauranteId) throws NotFoundException;
    void deleteByIdRestaurante(Long restauranteId) throws NotFoundException, BadRequestException;
    void update(Long restauranteId, String nombre) throws NotFoundException;
}
