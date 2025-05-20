package utn.back.mordiscoapi.service;

import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.DireccionDTO;
import utn.back.mordiscoapi.model.dto.PromocionDTO;
import utn.back.mordiscoapi.model.projection.DireccionProjection;
import utn.back.mordiscoapi.model.projection.PromocionProjection;

import java.util.List;

public interface DireccionService {
    void save(DireccionDTO dto) throws BadRequestException;
    List<DireccionProjection> findAll();
    DireccionProjection findById(Long id) throws NotFoundException;
    void update(Long id, DireccionDTO dto) throws NotFoundException,BadRequestException;
    void delete(Long id) throws NotFoundException;
}
