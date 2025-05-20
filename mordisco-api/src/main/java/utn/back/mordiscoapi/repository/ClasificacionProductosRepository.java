package utn.back.mordiscoapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.CalificacionProducto;
import utn.back.mordiscoapi.model.projection.ClasificacionProductoProjection;

import java.util.List;
import java.util.Optional;
@Repository
public interface ClasificacionProductosRepository extends JpaRepository<CalificacionProducto,Long> {
    @Query("""
            SELECT
                c.id AS id,
                c.producto AS producto,
                c.usuario AS usuario,
                c.puntaje AS puntaje,
                c.comentario AS comentario,
                c.fecha AS fecha
            FROM CalificacionProducto c
            """)
    List<ClasificacionProductosRepository> findAllProject();

    @Query("""
            SELECT
                c.id AS id,
                c.producto AS producto,
                c.usuario AS usuario,
                c.puntaje AS puntaje,
                c.comentario AS comentario,
                c.fecha AS fecha
            FROM CalificacionProducto c
            WHERE c.id = :id
            """)
    Optional<ClasificacionProductoProjection> findProjectById(Long id);

}
