package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;

import java.util.List;

public interface IHorarioService {
    Long save(Long idRestaurante,HorarioAtencionRequestDTO horario) throws NotFoundException;
    List<HorarioAtencionResponseDTO> findAllByIdRestaurante(Long idRestaurante) throws NotFoundException;
    void update(Long idHorario,HorarioAtencionRequestDTO horario) throws NotFoundException;
    void delete(Long idHorario) throws NotFoundException;
}
