package utn.back.mordiscoapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Producto;

import java.util.List;

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

    boolean existsByIdAndMenu_Restaurante_Usuario_Id(Long id, Long usuarioId);

    @Query("SELECT p FROM Producto p WHERE p.id IN :productosIds AND p.menu.restaurante.id = :restauranteId")
    List<Producto> findAllByIdInAndMenuRestauranteId(@Param("productosIds") List<Long> productosIds,
                                                      @Param("restauranteId") Long restauranteId);

    /**
     * Encuentra los productos más vendidos de un restaurante (ingresos después de descontar comisión de la plataforma)
     * Usa datos desnormalizados de productos_pedidos para incluir productos eliminados en estadísticas históricas.
     * NOTA: producto_id puede ser NULL para productos que fueron eliminados.
     * @param restauranteId ID del restaurante
     * @return Lista de productos más vendidos (producto_id puede ser NULL si el producto fue eliminado)
     */
    @Query(value = """
            SELECT pp.producto_id, pp.nombre_producto,
                   SUM(pp.cantidad) as cantidad_vendida,
                   SUM(
                       pp.precio_unitario * pp.cantidad *
                       (SELECT cs.porcentaje_ganancias_restaurante
                        FROM configuracion_sistema cs
                        ORDER BY cs.fecha_actualizacion DESC
                        LIMIT 1) / 100
                   ) as ingreso_generado
            FROM productos_pedidos pp
            JOIN pedidos p ON pp.pedido_id = p.id
            WHERE p.restaurante_id = :restauranteId
            AND p.estado = 'COMPLETADO'
            GROUP BY pp.producto_id, pp.nombre_producto
            ORDER BY cantidad_vendida DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findProductosMasVendidos(@Param("restauranteId") Long restauranteId);

}

