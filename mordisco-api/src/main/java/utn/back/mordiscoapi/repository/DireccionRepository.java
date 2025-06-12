package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
}
