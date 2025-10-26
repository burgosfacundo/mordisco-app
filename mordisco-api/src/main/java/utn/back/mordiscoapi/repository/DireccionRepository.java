package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.back.mordiscoapi.model.entity.Direccion;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByIdAndUsuarioId(Long id, Long usuarioId);
    List<Direccion> findAllByUsuarioId(Long usuarioId);
}
