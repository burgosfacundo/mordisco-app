package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;

import java.util.List;

public interface IRestauranteService {
    void save(RestauranteCreateDTO restauranteCreateDTO);
    RestauranteResponseDTO findById(Long id) throws NotFoundException;
    RestauranteResponseDTO findByIdUsuario(Long idUsuario) throws NotFoundException, BadRequestException;
    List<RestauranteResponseDTO> getAll();
    List<RestauranteResponseDTO> getAllByEstado(Boolean estado);
    List<RestauranteResponseCardDTO> getAllByCiudad(String ciudad);
    List<RestauranteResponseCardDTO> getAllByNombre(String nombre);
    List<RestauranteResponseCardDTO> findAllWithPromocionActivaAndCiudad(String ciudad);
    void update(RestauranteUpdateDTO dto) throws NotFoundException, BadRequestException;
    void delete(Long id) throws NotFoundException;
    void adHorariosAtencion(Long idRestaurante, List<HorarioAtencionDTO> horarios) throws NotFoundException, BadRequestException;
}
