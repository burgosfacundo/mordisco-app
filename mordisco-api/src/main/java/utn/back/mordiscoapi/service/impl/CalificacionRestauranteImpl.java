package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.mapper.CalificacionRestauranteMapper;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.entity.CalificacionRestaurante;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;
import utn.back.mordiscoapi.repository.CalificacionRestauranteRepository;
import utn.back.mordiscoapi.repository.RestauranteRepository;
import utn.back.mordiscoapi.repository.UsuarioRepository;
import utn.back.mordiscoapi.service.CrudService;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionRestauranteImpl implements CrudService<CalificacionRestauranteDTO, CalificacionRestauranteProjection, Long> {
    private final CalificacionRestauranteRepository repository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Guarda una calificacion de restaurante.
     * @param dto DTO de la calificacion a guardar.
     * @throws BadRequestException si hay un error al guardar la calificacion.
     */
    @Override
    public void save(CalificacionRestauranteDTO dto) throws BadRequestException, NotFoundException {
        if (dto.fechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        if(restauranteRepository.findById(dto.restauranteId()).isEmpty()){
            throw new NotFoundException("El id del restaurante no existe");
        }
        if(usuarioRepository.findById(dto.usuarioId()).isEmpty()){
            throw new NotFoundException("El id del usuario no existe");
        }
        try{
            CalificacionRestaurante calificacionRestaurante = CalificacionRestauranteMapper.toCalificacionRestaurante(dto);
            repository.save(calificacionRestaurante);
        }catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new BadRequestException("Error al guardar la calificacion de restaurante");
        }
    }

    /**
     * Obtiene todas las calificaciones de restaurantes.
     * @return una lista de calificaciones de restaurantes.
     */
    @Override
    public List<CalificacionRestauranteProjection> findAll() {
        return  repository.findAllProjection();
    }

    @Override
    public CalificacionRestauranteProjection findById(Long Long) throws NotFoundException {
        return null;
    }

    /**
     * Elimina un calificacion por id
     * @param aLong id de la calificacion que se desea borrar
     * @throws NotFoundException si no encuentra la calificacion que se desea borrar
     */
    @Override
    public void delete(Long aLong) throws NotFoundException {
        if (!repository.existsById(aLong)) {
            throw new NotFoundException("No se encontro la claificacion que se desea borrar");
        }
        repository.deleteById(aLong);
    }
}
