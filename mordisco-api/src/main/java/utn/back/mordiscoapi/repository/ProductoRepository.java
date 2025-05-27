package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.projection.ProductoProjection;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto,Long> {

    @Query("""
        SELECT
             p
        FROM Producto p INNER JOIN Imagen i
        ON p.imagen_id = i.id
        INNER JOIN Menu m
        ON p.menu_id = m.id
        WHERE p.id = :id
        """) // Anotación para realizar una query personalizada JPQL
    Optional<ProductoProjection> findCompleteById(@Param("id") Long id);


}
