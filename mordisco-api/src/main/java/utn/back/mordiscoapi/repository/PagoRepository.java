package utn.back.mordiscoapi.repository;

import utn.back.mordiscoapi.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoId(Long pedidoId);

    Optional<Pago> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);

    Optional<Pago> findByMercadoPagoPreferenceId(String mercadoPagoPreferenceId);

    /**
     * Encuentra un pago por ID del pedido con todas las relaciones necesarias cargadas
     * para evitar LazyInitializationException en eventos asíncronos
     */
    @Query("""
            SELECT pa FROM Pago pa
            JOIN FETCH pa.pedido p
            LEFT JOIN FETCH p.cliente
            LEFT JOIN FETCH p.restaurante r
            LEFT JOIN FETCH r.usuario
            WHERE p.id = :pedidoId
            """)
    Optional<Pago> findByPedidoIdWithRelations(@Param("pedidoId") Long pedidoId);
}
