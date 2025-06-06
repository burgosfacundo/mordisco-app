package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findUsuarioByRol_Id(Long rolId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE Usuario u
           SET u.password = :newPassword
           WHERE u.id = :id
           """)
    void changePassword(String newPassword, Long id);

    Optional<Usuario> findByEmail(String email);
}
