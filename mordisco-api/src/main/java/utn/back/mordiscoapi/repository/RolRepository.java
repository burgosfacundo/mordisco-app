package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Rol;

import java.util.Optional;


public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String roleRepartidor);
}
