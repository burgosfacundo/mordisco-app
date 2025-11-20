package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Pedido;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {

    Page<Pedido> findAllByRestaurante_Id(Pageable pageable, @Param("id")Long id);

    Page<Pedido> findAllByCliente_IdAndEstado(Pageable pageable,@Param("id") Long id, @Param("estado") EstadoPedido estado);

    Page<Pedido> findAllByCliente_Id(Pageable pageable,Long id);

    Page<Pedido> findAllByRestaurante_IdAndEstado(Pageable pageable,@Param("id")Long id, @Param("estado") EstadoPedido estado);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Pedido SET estado = :nuevoEstado WHERE id =:id
            """)
    void changeState(@Param("id") Long id, @Param("nuevoEstado") EstadoPedido nuevoEstado);

    boolean existsByCliente_IdAndRestaurante_Id(Long clienteId, Long restauranteId);

    boolean existsByIdAndCliente_Id(Long id, Long clienteId);
    @Modifying
    @Query("""
            UPDATE Pedido p SET p.direccionEntrega = NULL
            WHERE p.direccionEntrega.id = :direccionId""")
    void desasociarDireccion(@Param("direccionId") Long direccionId);


    /**
     * Encuentra pedidos disponibles para repartidores cerca de una ubicación
     */
    @Query(value =
            "SELECT p.* FROM pedidos p " +
                    "INNER JOIN direcciones d ON p.direccion_id = d.id " +
                    "WHERE p.estado = 'EN_CAMINO' " +
                    "AND p.tipo_entrega = 'DELIVERY' " +
                    "AND p.repartidor_id IS NULL " +
                    "AND d.latitud IS NOT NULL " +
                    "AND d.longitud IS NOT NULL " +
                    "AND ( " +
                    "  6371 * acos( " +
                    "    cos(radians(:latitud)) * cos(radians(d.latitud)) * " +
                    "    cos(radians(d.longitud) - radians(:longitud)) + " +
                    "    sin(radians(:latitud)) * sin(radians(d.latitud)) " +
                    "  ) " +
                    ") <= :radioKm " +
                    "ORDER BY p.fecha_hora DESC",
            nativeQuery = true
    )
    Page<Pedido> findPedidosDisponiblesParaRepartidor(
            @Param("latitud") Double latitud,
            @Param("longitud") Double longitud,
            @Param("radioKm") Double radioKm,
            Pageable pageable
    );
}
