package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.RestauranteMapper;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteDTO;
import utn.back.mordiscoapi.model.dto.restaurante.RestauranteResponseDTO;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.CrudService;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements CrudService<RestauranteDTO, RestauranteResponseDTO,Long> {
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void save(RestauranteDTO restauranteDTO) throws BadRequestException {
        try{
            Restaurante restaurante = RestauranteMapper.toEntity(restauranteDTO);
            restauranteRepository.save(restaurante);
        }catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar restaurante");
        }
    }

    @Override
    public List<RestauranteResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public RestauranteResponseDTO findById(Long id) throws NotFoundException {
        Restaurante rest = restauranteRepository.findRestauranteById(id).orElseThrow(
                () -> new NotFoundException("Restaurante no encontrada"));

        return RestauranteMapper.toDTO(rest);


    }
    public RestauranteResponseDTO findProjectByDuenio (Long idUsuario) throws NotFoundException, BadRequestException {
        if(!usuarioRepository.existsById(idUsuario)){
                throw new NotFoundException("el usuario no fue encontrado!");
        }

        var exist = restauranteRepository.findByDuenio(idUsuario);
        if(exist.isEmpty()){
            throw new BadRequestException("El dueño buscado no tiene ningun restaurante");
        }

        return RestauranteMapper.toDTO(exist.get());
    }

    @Override
    public void delete(Long aLong) throws NotFoundException {
        if(!restauranteRepository.existsById(aLong)){
            throw new NotFoundException("El restaurante a borrar no fue encontrado");
        }
        restauranteRepository.deleteById(aLong);
    }



}
