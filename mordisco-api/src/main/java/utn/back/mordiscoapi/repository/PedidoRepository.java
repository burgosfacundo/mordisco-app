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

import java.util.List;

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
}
