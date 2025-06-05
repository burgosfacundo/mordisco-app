package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.projection.PedidoProjection;

import java.util.List;
import java.util.Optional;
@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    @Query("""
        SELECT p
        FROM Pedido p
        JOIN ProductoPedido pp ON p.id = pp.pedido.id
        JOIN Producto pr ON pp.producto.id = pr.id
        WHERE p.restaurante.id = :id
        """)
    List<PedidoProjection> findAllCompleteByRestaurante(@Param("id")Long id);


    @Query("""
    SELECT
        p.id AS id,
        p.cliente.id AS clienteId,
        p.restaurante.id AS restauranteId,
        p.direccionEntrega.id AS direccionId,
        p.tipoEntrega AS tipoEntrega,
        p.fechaHora AS fechaHora,
        p.estado AS estado,
        p.total AS total,
        pp.id AS productoPedidoId,
        pp.cantidad AS cantidad,
        pp.precioUnitario AS precioUnitario,
        pr.id AS productoId
    FROM Pedido p
    JOIN p.items pp
    JOIN pp.producto pr
    """)
    List<PedidoProjection> findAllDTO ();

    @Query("""
       SELECT
            p.id AS id,
            p.cliente.id AS clienteId,
            p.restaurante.id AS restauranteId,
            p.direccionEntrega.id AS direccionId,
            p.tipoEntrega AS tipoEntrega,
            p.fechaHora AS fechaHora,
            p.estado AS estado,
            p.total AS total,
            pp.id AS productoPedidoId,
            pp.cantidad AS cantidad,
            pp.precioUnitario AS precioUnitario,
            pr.id AS productoId
        FROM Pedido p
        JOIN p.items pp
        JOIN pp.producto pr
        WHERE p.id = :id
       """)
    Optional<PedidoProjection> findByProjectID(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Pedido SET estado = :nuevoEstado WHERE id =:id
            """)
    void changeState(@Param("id") Long id, @Param("nuevoEstado") EstadoPedido nuevoEstado);

    @Query("""
            SELECT
                p.id AS id,
                p.cliente.id AS clienteId,
                p.restaurante.id AS restauranteId,
                p.direccionEntrega.id AS direccionId,
                p.tipoEntrega AS tipoEntrega,
                p.fechaHora AS fechaHora,
                p.estado AS estado,
                p.total AS total,
                pp.id AS productoPedidoId,
                pp.cantidad AS cantidad,
                pp.precioUnitario AS precioUnitario,
                pr.id AS productoId
            FROM Pedido p
            JOIN p.items pp
            JOIN pp.producto pr
            WHERE
                p.cliente.id = :id AND p.estado = :estado
            """)
    List<PedidoProjection> findAllXClientesXEstado(@Param("id") Long id, @Param("estado") EstadoPedido estado);

    @Query("""
            SELECT
                p.id AS id,
                p.cliente.id AS clienteId,
                p.restaurante.id AS restauranteId,
                p.direccionEntrega.id AS direccionId,
                p.tipoEntrega AS tipoEntrega,
                p.fechaHora AS fechaHora,
                p.estado AS estado,
                p.total AS total,
                pp.id AS productoPedidoId,
                pp.cantidad AS cantidad,
                pp.precioUnitario AS precioUnitario,
                pr.id AS productoId
            FROM Pedido p
            JOIN p.items pp
            JOIN pp.producto pr
            WHERE p.cliente.id = :id
            """)
    List<PedidoProjection> findAllXClientes(Long id);


    @Query("""
            SELECT
                p.id AS id,
                p.cliente.id AS clienteId,
                p.restaurante.id AS restauranteId,
                p.direccionEntrega.id AS direccionId,
                p.tipoEntrega AS tipoEntrega,
                p.fechaHora AS fechaHora,
                p.estado AS estado,
                p.total AS total,
                pp.id AS productoPedidoId,
                pp.cantidad AS cantidad,
                pp.precioUnitario AS precioUnitario,
                pr.id AS productoId
            FROM Pedido p
            JOIN p.items pp
            JOIN pp.producto pr
            WHERE p.restaurante.id = :id AND p.estado = :estado
            """)
    List<PedidoProjection> findAllXRestauranteXEstado(@Param("id")Long id, @Param("estado") EstadoPedido estado);


    @Query("""
            SELECT
                count(p.id) AS cantidad_pedidos
            FROM Pedido p
            WHERE p.restaurante.id = :id AND p.estado = :estado
            """)
    Long cantidadPedidosXEstado(Long id, EstadoPedido estado);
}
