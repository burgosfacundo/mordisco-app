package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.projection.ImagenProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen,Long> {
    @Query("""
           SELECT
                i.id AS id,
                i.url AS url,
                i.nombre AS nombre
           FROM Imagen i
           """)
    List<ImagenProjection> findAllProject();

    @Query ("""
            SELECT
                i.id AS id,
                i.url AS url,
                i.nombre AS nombre
           FROM Imagen i
           WHERE i.id = :id
           """)
    Optional<ImagenProjection> findProjectById(Long id);
}
