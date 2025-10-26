package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;

import java.util.List;

public interface IDireccionService {
    List<DireccionResponseDTO> list(Long usuarioId);
    Long add(Long usuarioId, DireccionCreateDTO dto) throws NotFoundException;
    void update(Long usuarioId, DireccionUpdateDTO dto) throws NotFoundException;
    void delete(Long usuarioId, Long dirId) throws NotFoundException;
}
