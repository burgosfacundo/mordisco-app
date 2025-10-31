package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.calificacionRestaurante.CalificacionRestauranteDTO;
import utn.back.mordiscoapi.model.projection.CalificacionRestauranteProjection;


public interface ICalificacionRestaurante {
    void save(CalificacionRestauranteDTO dto) throws NotFoundException, BadRequestException;
    Page<CalificacionRestauranteProjection> findAll(int pageNo, int pageSize);
    void delete(Long aLong) throws NotFoundException;
}
