package utn.back.mordiscoapi.repository;

import utn.back.mordiscoapi.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoId(Long pedidoId);

    Optional<Pago> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);

    Optional<Pago> findByMercadoPagoPreferenceId(String mercadoPagoPreferenceId);
}
