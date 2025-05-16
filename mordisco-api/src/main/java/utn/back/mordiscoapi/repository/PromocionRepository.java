package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entities.Promocion;
import utn.back.mordiscoapi.model.projection.PromocionProjection;
@Repository
public interface PromocionRepository extends JpaRepository<Promocion,Long> {
    Page<PromocionProjection> findAllProject(Pageable pageable);
}
