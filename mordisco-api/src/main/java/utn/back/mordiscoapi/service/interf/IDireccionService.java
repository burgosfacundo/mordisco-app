package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.common.exception.BadRequestException;
import utn.back.mordiscoapi.common.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.direccion.DireccionRequestDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;

import java.util.List;

public interface IDireccionService {
    List<DireccionResponseDTO> getMisDirecciones() throws BadRequestException;
    DireccionResponseDTO createMiDireccion(DireccionRequestDTO dto) throws BadRequestException;
    DireccionResponseDTO updateMiDireccion(Long id,DireccionRequestDTO dto)
            throws NotFoundException, BadRequestException;
    void deleteMiDireccion(Long id) throws NotFoundException, BadRequestException;
    List<DireccionResponseDTO> getByUsuarioId(Long userId) throws NotFoundException;
}
