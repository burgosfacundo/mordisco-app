package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;

import java.util.List;

public interface ICalificacionRestaurante {
    void save(CalificacionRestauranteDTO dto) throws NotFoundException;
    List<CalificacionRestauranteProjection> findAll();
    void delete(Long aLong) throws NotFoundException;
}
