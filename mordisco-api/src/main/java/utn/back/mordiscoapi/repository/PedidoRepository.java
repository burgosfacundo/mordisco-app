package utn.back.mordiscoapi.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.model.entity.Pedido;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {

    List<Pedido> findAllByRestaurante_Id(@Param("id")Long id);

    List<Pedido> findAllByCliente_IdAndEstado(@Param("id") Long id, @Param("estado") EstadoPedido estado);

    List<Pedido> findAllByCliente_Id(Long id);

    List<Pedido> findAllByRestaurante_IdAndEstado(@Param("id")Long id, @Param("estado") EstadoPedido estado);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Pedido SET estado = :nuevoEstado WHERE id =:id
            """)
    void changeState(@Param("id") Long id, @Param("nuevoEstado") EstadoPedido nuevoEstado);

    boolean existsByUsuarioIdAndRestauranteId(Long usuarioId, Long restauranteId);
}
