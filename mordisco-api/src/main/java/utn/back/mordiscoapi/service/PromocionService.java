package utn.back.mordiscoapi.service;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.PromocionDTO;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

import java.util.List;


/**
 * Interfaz que define los métodos para el servicio de promociones.
 */
public interface PromocionService {
    void save(PromocionDTO dto) throws BadRequestException;
    List<PromocionProjection> findAll();
    PromocionProjection findById(Long id) throws NotFoundException;
    void update(Long id, PromocionDTO dto) throws NotFoundException,BadRequestException;
    void delete(Long id) throws NotFoundException;
}
