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
              p.id AS id,
              p.clienteID AS clienteID,
              p.restauranteID AS restauranteID,
              p.direccionEntregaID AS direccionEntregaID,
              p.tipoEntrega AS tipoEntrega
              p.tipoPago AS tipoPago,
              p.fecahHora AS fechaHora,
              p.estado AS estado,
              p.total AS total
        FROM Pedidos p
        """) // Anotación para realizar una query personalizada JPQL
    List<Pedido> findAllByRestaurante();



}
