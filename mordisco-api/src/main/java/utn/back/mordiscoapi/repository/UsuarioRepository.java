package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findUsuarioByRol_Id(Long rolId);

    Optional<Usuario> findByEmail(String email);
}
