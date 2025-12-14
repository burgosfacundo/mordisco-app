package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;

public interface IRestauranteService {
    void save(RestauranteCreateDTO restauranteCreateDTO) throws BadRequestException;

    RestauranteResponseDTO findById(Long id) throws NotFoundException;

    RestauranteResponseDTO findByIdUsuario(Long idUsuario) throws NotFoundException, BadRequestException;

    Page<RestauranteResponseDTO> getAll(int pageNo, int pageSize);

    Page<RestauranteResponseDTO> getAllByEstado(int pageNo, int pageSize, Boolean estado);

    Page<RestauranteResponseCardDTO> getAllByCiudad(int pageNo, int pageSize, String ciudad);

    Page<RestauranteResponseCardDTO> getAllByNombre(int pageNo, int pageSize, String nombre);

    Page<RestauranteResponseCardDTO> findAllWithPromocionActivaAndCiudad(int pageNo, int pageSize, String ciudad);

    void update(RestauranteUpdateDTO dto) throws NotFoundException, BadRequestException;

    void delete(Long id) throws NotFoundException, BadRequestException;

    Page<PedidoResponseDTO> getPedidosActivos(Long restauranteId, int page, int size)
            throws NotFoundException;

    Page<RestauranteResponseDTO> filtrarRestaurantes(
            int pageNo,
            int pageSize,
            String search,
            String activo
    );
    /**
     * Busca restaurantes dentro de un radio desde una ubicación específica
     * @param latitud Latitud del punto de referencia
     * @param longitud Longitud del punto de referencia
     * @param radioKm Radio de búsqueda en kilómetros
     * @param searchTerm Término de búsqueda opcional (null o vacío para todos)
     * @param pageNo Número de página
     * @param pageSize Tamaño de página
     * @return Página de restaurantes ordenados por estado abierto y distancia
     */
    Page<RestauranteResponseCardDTO> findByLocationWithinRadius(
            Double latitud,
            Double longitud,
            Double radioKm,
            String searchTerm,
            int pageNo,
            int pageSize
    );

    /**
     * Busca restaurantes con promociones activas dentro de un radio desde una ubicación específica
     * @param latitud Latitud del punto de referencia
     * @param longitud Longitud del punto de referencia
     * @param radioKm Radio de búsqueda en kilómetros
     * @param pageNo Número de página
     * @param pageSize Tamaño de página
     * @return Página de restaurantes con promociones ordenados por estado abierto y distancia
     */
    Page<RestauranteResponseCardDTO> findWithPromocionByLocationWithinRadius(
            Double latitud,
            Double longitud,
            Double radioKm,
            int pageNo,
            int pageSize
    );
}
