package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.ImagenMapper;
import utn.back.mordiscoapi.mapper.PedidoMapper;
import utn.back.mordiscoapi.mapper.RestauranteMapper;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseCardDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.GeocodingService;
import utn.back.mordiscoapi.service.interf.IRestauranteService;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements IRestauranteService {
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeocodingService geocodingService;

    /**
     * Guarda un nuevo restaurante en la base de datos.
     *
     * @param restauranteCreateDTO DTO que contiene los detalles del restaurante a guardar.
     */
    @Transactional
    @Override
    public void save(RestauranteCreateDTO restauranteCreateDTO) {
        Restaurante restaurante = RestauranteMapper.toEntity(restauranteCreateDTO);
        var d = restaurante.getDireccion();

        geocodingService.geocode(d.getCalle(), d.getNumero(), d.getCiudad(), d.getCodigoPostal())
                .ifPresent(latLng -> {
                    d.setLatitud(latLng.lat());
                    d.setLongitud(latLng.lng());
                });

        restauranteRepository.save(restaurante);
    }

    /**
     * Busca un restaurante por su ID.
     *
     * @param id del restaurante a buscar.
     * @return RestauranteResponseDTO con los detalles del restaurante encontrado.
     * @throws NotFoundException si no se encuentra el restaurante con el ID proporcionado.
     */
    @Override
    public RestauranteResponseDTO findById(Long id) throws NotFoundException {
        Restaurante rest = restauranteRepository.findRestauranteById(id).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrado"));

        return RestauranteMapper.toDTO(rest);
    }

    /**
     * Busca un restaurante por el ID del usuario que lo posee.
     *
     * @param idUsuario ID del usuario dueño del restaurante.
     * @return RestauranteResponseDTO con los detalles del restaurante encontrado.
     * @throws NotFoundException si el usuario no existe.
     * @throws BadRequestException si el usuario no tiene un restaurante asociado.
     */
    @Override
    public RestauranteResponseDTO findByIdUsuario(Long idUsuario) throws NotFoundException, BadRequestException {
        if(!usuarioRepository.existsById(idUsuario)){
                throw new NotFoundException("El usuario no existe");
        }

        var exist = restauranteRepository.findByIdUsuario(idUsuario);
        if(exist.isEmpty()){
            throw new BadRequestException("El usuario no tiene un restaurante asociado");
        }

        return RestauranteMapper.toDTO(exist.get());
    }

    /**
     * Lista todos los restaurantes.
     *
     * @return Lista de RestauranteResponseDTO con todos los restaurantes.
     */
    @Override
    public Page<RestauranteResponseDTO> getAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return restauranteRepository.findAllRestaurante(pageable)
                .map(RestauranteMapper::toDTO);
    }

    /**
     * Lista los restaurantes por su estado (activo o inactivo).
     *
     * @param estado Estado del restaurante (true para activo, false para inactivo).
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por estado.
     */
    @Override
    public Page<RestauranteResponseDTO> getAllByEstado(int pageNo, int pageSize,Boolean estado){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return restauranteRepository.findAllByEstado(pageable,estado)
                .map(RestauranteMapper::toDTO);
    }

    /**
     * Lista los restaurantes por ciudad.
     *
     * @param ciudad Ciudad donde se encuentran los restaurantes.
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por ciudad.
     */
    @Override
    public Page<RestauranteResponseCardDTO> getAllByCiudad(int pageNo, int pageSize,String ciudad){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return restauranteRepository.findAllByCiudad(pageable,ciudad)
                .map(RestauranteMapper::toCardDTO);
    }

    /**
     * Lista los restaurantes por nombre.
     *
     * @param nombre Nombre del restaurante a buscar.
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por nombre.
     */
    @Override
    public Page<RestauranteResponseCardDTO> getAllByNombre(int pageNo, int pageSize,String nombre) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return restauranteRepository.findAllByNombre(pageable,nombre)
                .map(RestauranteMapper::toCardDTO);
    }

    /**
     * Lista los restaurantes que tienen una promoción activa.
     * @return Lista de RestauranteResponseDTO con los restaurantes que tienen una promoción activa.
     */
    @Override
    public Page<RestauranteResponseCardDTO> findAllWithPromocionActivaAndCiudad(int pageNo, int pageSize,String ciudad) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return restauranteRepository.findAllWithPromocionActivaAndCiudad(pageable,ciudad)
                .map(RestauranteMapper::toCardDTO);
    }

    /**
     * Actualiza un restaurante existente.
     *
     * @param dto DTO que contiene los nuevos detalles del restaurante.
     * @throws NotFoundException si el restaurante a actualizar no se encuentra.
     */
    @Transactional
    @Override
    public void update(RestauranteUpdateDTO dto) throws NotFoundException, BadRequestException {
        var restaurante = restauranteRepository.findRestauranteById(dto.id())
                .orElseThrow(() -> new NotFoundException("El restaurante a actualizar no fue encontrado"));

        if (!restauranteRepository.existsByIdAndImagen_Id(dto.id(), dto.logo().id())) {
            throw new BadRequestException("La imagen ya está asociada a otro restaurante");
        }

        restaurante.setActivo(dto.activo());
        restaurante.setRazonSocial(dto.razonSocial());
        ImagenMapper.applyUpdate(dto.logo(),restaurante.getImagen());

        restauranteRepository.save(restaurante);
    }

    /**
     * Elimina un restaurante validando que no tenga pedidos activos
     *
     * @param id del restaurante a eliminar
     * @throws NotFoundException si no se encuentra el restaurante
     */
    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException, BadRequestException {
        if (!restauranteRepository.existsById(id)) {
            throw new NotFoundException("El restaurante no existe");
        }

        long pedidosActivos = restauranteRepository.countPedidosActivos(id);

        if (pedidosActivos > 0) {
            String mensaje = String.format(
                    "No se puede eliminar el restaurante. Tiene %d pedido%s activo%s. " +
                            "Debe esperar a que se completen o cancelarlos antes de eliminarlo.",
                    pedidosActivos,
                    pedidosActivos == 1 ? "" : "s",
                    pedidosActivos == 1 ? "" : "s"
            );

            throw new BadRequestException(mensaje);
        }

        restauranteRepository.deleteById(id);
    }

    /**
     *  Obtiene los pedidos activos de un restaurante (para mostrar al usuario por qué no puede eliminar)
     */
    @Override
    public Page<PedidoResponseDTO> getPedidosActivos(Long restauranteId, int page, int size)
            throws NotFoundException {

        if (!restauranteRepository.existsById(restauranteId)) {
            throw new NotFoundException("El restaurante no existe");
        }

        Pageable pageable = PageRequest.of(page, size);
        return restauranteRepository.findPedidosActivosByRestaurante(restauranteId, pageable)
                .map(PedidoMapper::toDTO);
    }
}
