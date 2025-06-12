package utn.back.mordiscoapi.service.interf;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

import java.util.List;

public interface IPromocionService {
    void save(PromocionRequestDTO dto) throws BadRequestException;
    List<PromocionProjection> findAll();
    PromocionProjection findById(Long id) throws NotFoundException;
    void update(Long id, PromocionRequestDTO dto) throws NotFoundException,BadRequestException;
    void delete(Long id) throws NotFoundException;
    List<PromocionResponseDTO> listarPromoPorRestaurante (Long idRestaurante)throws NotFoundException;
}
