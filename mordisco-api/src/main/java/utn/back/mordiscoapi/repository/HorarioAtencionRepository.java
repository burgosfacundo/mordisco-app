package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.HorarioAtencion;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {
}
