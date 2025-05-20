package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.dto.PedidoDTOResponse;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.util.List;
import java.util.Optional;
@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    @Query("""
        SELECT
             p,pp,pr
        FROM Pedido p INNER JOIN PedidoProducto pp
            ON p.id = pp.pedido_id
            INNER JOIN Producto pr
            ON pp.producto_id = pr.id
        WHERE p.id = :id
        """) // Anotación para realizar una query personalizada JPQL
    Optional<Pedido> findCompletById(@Param("id") Long id);

    @Query("""
        SELECT
             p,pp,pr
        FROM Pedido p INNER JOIN PedidoProducto pp
            ON p.id = pp.pedido_id
            INNER JOIN Producto pr
            ON pp.producto_id = pr.id
        WHERE 
            p.restaurante_id = :id
            """)
    List<Pedido> findAllByRestaurante(@Param("id")Long id);
}
