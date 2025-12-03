package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("""
        SELECT
             p
        FROM Producto p
        INNER JOIN Imagen i ON p.imagen.id = i.id
        WHERE p.menu.id = :idMenu
        """)
    Page<Producto> findAllByIdMenu(Pageable pageable,Long idMenu);

    @Query("SELECT COUNT(DISTINCT pp.pedido) FROM ProductoPedido pp " +
            "WHERE pp.producto.id = :productoId " +
            "AND pp.pedido.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO')")
    long countPedidosActivosByProducto(@Param("productoId") Long productoId);

    @Query("SELECT DISTINCT pp.pedido FROM ProductoPedido pp " +
            "WHERE pp.producto.id = :productoId " +
            "AND pp.pedido.estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO') " +
            "ORDER BY pp.pedido.fechaHora DESC")
    Page<Pedido> findPedidosActivosByProducto(
            @Param("productoId") Long productoId,
            Pageable pageable
    );

    /**
     * Encuentra los productos más vendidos de un restaurante
     * @param restauranteId ID del restaurante
     * @return Lista de productos más vendidos
     */
    @Query(value = """
            SELECT pr.id, pr.nombre,
                   SUM(pp.cantidad) as cantidad_vendida,
                   SUM(pp.precio_unitario * pp.cantidad) as ingreso_generado
            FROM productos pr
            JOIN productos_pedido pp ON pp.producto_id = pr.id
            JOIN pedidos p ON pp.pedido_id = p.id
            WHERE pr.restaurante_id = :restauranteId
            AND p.estado = 'COMPLETADO'
            GROUP BY pr.id, pr.nombre
            ORDER BY cantidad_vendida DESC
            LIMIT 10
            """, nativeQuery = true)
    java.util.List<Object[]> findProductosMasVendidos(@Param("restauranteId") Long restauranteId);
}

