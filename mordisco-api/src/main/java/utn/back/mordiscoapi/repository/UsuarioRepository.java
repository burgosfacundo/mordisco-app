package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findUsuarioByRol_Id(Pageable pageable,Long rolId);

    Optional<Usuario> findByEmail(String email);
}
