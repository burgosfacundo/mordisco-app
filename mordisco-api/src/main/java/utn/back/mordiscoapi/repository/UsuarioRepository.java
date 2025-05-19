package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Usuario;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("""
        SELECT
            u.id AS id,
            u.nombre AS nombre,
            u.apellido AS apellido,
            u.telefono AS telefono,
            u.email AS email,
            u.rol.id AS rolId
        FROM Usuario u
    """)
    List<UsuarioProjection> findAllProject();

    @Query("""
        SELECT
            u.id AS id,
            u.nombre AS nombre,
            u.apellido AS apellido,
            u.telefono AS telefono,
            u.email AS email,
            u.rol.id AS rolId
        FROM Usuario u
        WHERE u.id = :id
    """)
    Optional<UsuarioProjection> findProjectById(Long id);

    @Query("""
        SELECT
            u.id AS id,
            u.nombre AS nombre,
            u.apellido AS apellido,
            u.telefono AS telefono,
            u.email AS email,
            u.rol.id AS rolId
        FROM Usuario u
        WHERE u.rol.id = :id
    """)
    List<UsuarioProjection> findProjectByRol(Long id);

    @Modifying
    @Transactional
    @Query("""
        UPDATE
            Usuario u
       SET
           u.password = :newPassword
       WHERE
           u.id = :id
    """)
    void changePassword(String newPassword, Long id);
}
