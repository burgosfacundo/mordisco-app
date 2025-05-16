package utn.back.mordiscoapi.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import utn.back.mordiscoapi.model.projection.PromocionProjection;


public interface PromocionService {
    void agregarPromocion();
    void eliminarPromocion();
    void modificarPromocion();
    Page<PromocionProjection> traerTodos(Pageable pageable);
}
