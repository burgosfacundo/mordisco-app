package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.util.List;
import java.util.Optional;
@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    @Query("""
        SELECT
             p,pp,pr
        FROM Pedido p
        JOIN ProductoPedido pp ON p.id = pp.pedido.id
        JOIN Producto pr ON pp.producto.id = pr.id
        WHERE p.id = :id
        """) // Anotación para realizar una query personalizada JPQL
    Optional<Pedido> findCompleteById(@Param("id") Long id);

    @Query("""
        SELECT
             p,pp,pr
        FROM Pedido p
        JOIN ProductoPedido pp ON p.id = pp.pedido.id
        JOIN Producto pr ON pp.producto.id = pr.id
        WHERE p.restaurante.id = :id
        """)
    List<Pedido> findAllCompleteByRestaurante(@Param("id")Long id);
}
