package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionRequestDTO;
import utn.back.mordiscoapi.model.dto.horarioAtencion.HorarioAtencionResponseDTO;

public interface IHorarioService {
    void save(Long idRestaurante,HorarioAtencionRequestDTO horario) throws NotFoundException;
    Page<HorarioAtencionResponseDTO> findAllByIdRestaurante(int page,int size,Long idRestaurante) throws NotFoundException;
    void update(Long idHorario,HorarioAtencionRequestDTO horario) throws NotFoundException;
    void delete(Long idHorario) throws NotFoundException;
}
