package utn.back.mordiscoapi.service.interf;

import org.springframework.data.domain.Page;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

public interface IPromocionService {
    void save(PromocionRequestDTO dto) throws BadRequestException;
    Page<PromocionProjection> findAll(int pageNo,int pageSize);
    PromocionProjection findById(Long id) throws NotFoundException;
    void update(Long id, PromocionRequestDTO dto) throws NotFoundException,BadRequestException;
    void delete(Long idRestaurante,Long idPromocion) throws NotFoundException;
    Page<PromocionResponseDTO> listarPromoPorRestaurante (int pageNo,int pageSize,Long idRestaurante)throws NotFoundException;
}
