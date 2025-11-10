package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;

public interface IRestauranteService {
    void save(RestauranteCreateDTO restauranteCreateDTO);

    RestauranteResponseDTO findById(Long id) throws NotFoundException;

    RestauranteResponseDTO findByIdUsuario(Long idUsuario) throws NotFoundException, BadRequestException;

    Page<RestauranteResponseDTO> getAll(int pageNo, int pageSize);

    Page<RestauranteResponseDTO> getAllByEstado(int pageNo, int pageSize, Boolean estado);

    Page<RestauranteResponseCardDTO> getAllByCiudad(int pageNo, int pageSize, String ciudad);

    Page<RestauranteResponseCardDTO> getAllByNombre(int pageNo, int pageSize, String nombre);

    Page<RestauranteResponseCardDTO> findAllWithPromocionActivaAndCiudad(int pageNo, int pageSize, String ciudad);

    void update(RestauranteUpdateDTO dto) throws NotFoundException, BadRequestException;

    void delete(Long id) throws NotFoundException;
}
