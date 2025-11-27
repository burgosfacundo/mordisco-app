package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

import java.util.List;

public interface HorarioRepository extends JpaRepository<HorarioAtencion,Long> {
    List<HorarioAtencion> findAllByRestauranteId(Long restauranteId);
}
