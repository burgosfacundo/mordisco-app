package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;

import java.util.List;

public interface IRestauranteService {
    void save(RestauranteCreateDTO restauranteCreateDTO);
    RestauranteResponseDTO findById(Long id) throws NotFoundException;
    RestauranteResponseDTO findByIdUsuario(Long idUsuario) throws NotFoundException, BadRequestException;
    List<RestauranteResponseDTO> getAll();
    List<RestauranteResponseDTO> getAllByEstado(Boolean estado);
    List<RestauranteResponseDTO> getAllByCiudad(String ciudad);
    List<RestauranteResponseDTO> getAllByNombre(String nombre);
    List<RestauranteResponseDTO> getAllByPromocionActiva();
    void update(RestauranteUpdateDTO dto) throws NotFoundException;
    void delete(Long id) throws NotFoundException;
    void adHorariosAtencion(Long idRestaurante, List<HorarioAtencionDTO> horarios) throws NotFoundException;
}
