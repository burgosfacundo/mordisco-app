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
     * Calcula la distancia TOTAL del recorrido:
     * 1. Desde ubicación del repartidor hasta el RESTAURANTE (recogida)
     * 2. Desde el RESTAURANTE hasta la DIRECCIÓN DE ENTREGA
     */
    @Query(value =
            "SELECT p.* FROM pedidos p " +
                    "INNER JOIN restaurantes r ON p.restaurante_id = r.id " +
                    "INNER JOIN direcciones dr ON r.direccion_id = dr.id " +
                    "INNER JOIN direcciones de ON p.direccion_id = de.id " +
                    "WHERE p.estado = 'LISTO_PARA_ENTREGAR' " +
                    "AND p.tipo_entrega = 'DELIVERY' " +
                    "AND p.repartidor_id IS NULL " +
                    "AND dr.latitud IS NOT NULL " +
                    "AND dr.longitud IS NOT NULL " +
                    "AND de.latitud IS NOT NULL " +
                    "AND de.longitud IS NOT NULL " +
                    "AND ( " +
                    "  ( " +
                    "    6371 * acos( " +
                    "      cos(radians(:latitud)) * cos(radians(dr.latitud)) * " +
                    "      cos(radians(dr.longitud) - radians(:longitud)) + " +
                    "      sin(radians(:latitud)) * sin(radians(dr.latitud)) " +
                    "    ) " +
                    "  ) + ( " +
                    "    6371 * acos( " +
                    "      cos(radians(dr.latitud)) * cos(radians(de.latitud)) * " +
                    "      cos(radians(de.longitud) - radians(dr.longitud)) + " +
                    "      sin(radians(dr.latitud)) * sin(radians(de.latitud)) " +
                    "    ) " +
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
