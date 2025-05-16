package utn.back.mordiscoapi.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
import utn.back.mordiscoapi.repository.PromocionRepository;
import utn.back.mordiscoapi.service.PromocionService;

@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {
    private PromocionRepository promocionRepository;

    @Override
    public void agregarPromocion() {

    }

    @Override
    public void eliminarPromocion() {

    }

    @Override
    public void modificarPromocion() {

    }

    @Override
    public Page<PromocionProjection> traerTodos(Pageable pageable) {
        return promocionRepository.findAllProject(pageable);
    }

}
