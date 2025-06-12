package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
}
