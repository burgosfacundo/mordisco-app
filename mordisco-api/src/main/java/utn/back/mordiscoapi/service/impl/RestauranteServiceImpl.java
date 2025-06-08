package utn.back.mordiscoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.DireccionMapper;
import utn.back.mordiscoapi.mapper.HorarioAtencionMapper;
import utn.back.mordiscoapi.mapper.ImagenMapper;
import utn.back.mordiscoapi.mapper.RestauranteMapper;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteCreateDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteUpdateDTO;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.interf.IRestauranteService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements IRestauranteService {
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Guarda un nuevo restaurante en la base de datos.
     *
     * @param restauranteCreateDTO DTO que contiene los detalles del restaurante a guardar.
     */
    @Transactional
    @Override
    public void save(RestauranteCreateDTO restauranteCreateDTO) {
        Restaurante restaurante = RestauranteMapper.toEntity(restauranteCreateDTO);
        restaurante.setActivo(false);
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
                () -> new NotFoundException("Restaurante no encontrada"));

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
                throw new NotFoundException("el usuario no fue encontrado!");
        }

        var exist = restauranteRepository.findByIdUsuario(idUsuario);
        if(exist.isEmpty()){
            throw new BadRequestException("El dueño buscado no tiene ningun restaurante");
        }

        return RestauranteMapper.toDTO(exist.get());
    }

    /**
     * Lista todos los restaurantes.
     *
     * @return Lista de RestauranteResponseDTO con todos los restaurantes.
     */
    @Override
    public List<RestauranteResponseDTO> getAll() {
        return restauranteRepository.findAllRestaurante().stream()
                .map(RestauranteMapper::toDTO)
                .toList();
    }

    /**
     * Lista los restaurantes por su estado (activo o inactivo).
     *
     * @param estado Estado del restaurante (true para activo, false para inactivo).
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por estado.
     */
    @Override
    public List<RestauranteResponseDTO> getAllByEstado(Boolean estado){
        return restauranteRepository.findAllByEstado(estado).stream()
                .map(RestauranteMapper::toDTO)
                .toList();
    }

    /**
     * Lista los restaurantes por ciudad.
     *
     * @param ciudad Ciudad donde se encuentran los restaurantes.
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por ciudad.
     */
    @Override
    public List<RestauranteResponseDTO> getAllByCiudad(String ciudad){
        return restauranteRepository.findAllByCiudad(ciudad).stream()
                .map(RestauranteMapper::toDTO)
                .toList();
    }

    /**
     * Lista los restaurantes por nombre.
     *
     * @param nombre Nombre del restaurante a buscar.
     * @return Lista de RestauranteResponseDTO con los restaurantes filtrados por nombre.
     */
    @Override
    public List<RestauranteResponseDTO> getAllByNombre(String nombre) {
        return restauranteRepository.findAllByNombre(nombre).stream()
                .map(RestauranteMapper::toDTO)
                .toList();
    }

    /**
     * Lista los restaurantes que tienen una promoción activa.
     * @return Lista de RestauranteResponseDTO con los restaurantes que tienen una promoción activa.
     */
    @Override
    public List<RestauranteResponseDTO> getAllByPromocionActiva() {
        return restauranteRepository.findAllWithPromocionActiva().stream()
                .map(RestauranteMapper::toDTO)
                .toList();
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
        if (!restauranteRepository.existsByIdAndDireccion_Id(dto.id(),dto.direccion().id())) {
            throw new BadRequestException("La dirección ya está asociada a otro restaurante");
        }

        restaurante.setActivo(dto.activo());
        restaurante.setRazonSocial(dto.razonSocial());
        restaurante.setImagen(ImagenMapper.updateToEntity(dto.logo()));
        restaurante.setDireccion(DireccionMapper.updateToEntity(dto.direccion()));

        restauranteRepository.save(restaurante);
    }

    /**
     * Elimina un restaurante por su ID.
     *
     * @param id del restaurante a eliminar.
     * @throws NotFoundException si no se encuentra el restaurante con el ID proporcionado.
     */
    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException {
        if(!restauranteRepository.existsById(id)){
            throw new NotFoundException("El restaurante a borrar no fue encontrado");
        }
        restauranteRepository.deleteById(id);
    }

    /**
     * Agrega o modifica horarios de atención a un restaurante.
     *
     * @param idRestaurante ID del restaurante al que se le agregarán o modificarán los horarios.
     * @param horarios Lista de horarios de atención a agregar.
     * @throws NotFoundException si el restaurante no se encuentra.
     */
    @Transactional
    @Override
    public void adHorariosAtencion(Long idRestaurante, List<HorarioAtencionDTO> horarios) throws NotFoundException {
        Restaurante restaurante = restauranteRepository.findRestauranteById(idRestaurante)
                .orElseThrow(() -> new NotFoundException("El restaurante no fue encontrado"));

        var list = horarios.stream()
                .map(HorarioAtencionMapper::toEntity)
                .collect(Collectors.toSet());

        restaurante.getHorariosAtencion().clear();
        restaurante.getHorariosAtencion().addAll(list);
        restauranteRepository.save(restaurante);
    }
}
