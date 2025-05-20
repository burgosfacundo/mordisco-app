package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.projection.ProductoProjection;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {
    @Query("""
               SELECT
                    p.id AS id,
                    p.nombre AS nombre,
                    p.descripcion AS descripcion,
                    p.precio AS precio,
                    p.disponible AS disponibleOno,
                    p.imagen AS imagen
               FROM Producto p
            """)
    List<ProductoProjection> findAllProject();
    @Query("""
               SELECT
                    p.id AS id,
                    p.nombre AS nombre,
                    p.descripcion AS descripcion,
                    p.precio AS precio,
                    p.disponible AS disponibleOno,
                    p.imagen AS imagen
               FROM Producto p
               WHERE p.id = :id
            """)
    Optional<ProductoProjection> findProjectById(Long id);

    @Query("""
            SELECT
             p.id AS id,
                    p.nombre AS nombre,
                    p.descripcion AS descripcion,
                    p.precio AS precio,
                    p.disponible AS disponibleOno,
                    p.imagen AS imagen
               FROM Producto p
               WHERE p.precio BETWEEN :precioI AND :precioF
            """)
    List<ProductoProjection> findBetweenPrices(Double precioI, Double precioF);
}
