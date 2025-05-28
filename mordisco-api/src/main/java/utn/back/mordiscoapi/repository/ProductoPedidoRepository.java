package utn.back.mordiscoapi.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.ProductoPedido;
import utn.back.mordiscoapi.model.projection.ProductoPedidoProjection;

import java.util.List;

public interface ProductoPedidoRepository extends CrudRepository<ProductoPedido, Long> {
    @Query("""
            SELECT
                  pp.id AS id,
                  pp.cantidad AS cantidad,
                  pp.precioUnitario AS precioUnitario,
                  pp.pedido.id AS pedidoId,
                  pp.producto.id AS productoId
            FROM ProductoPedido pp
            WHERE pp.pedido.id = :idPedido 
            """)
    List<ProductoPedidoProjection> findByIdPedido(Long idPedido);
}
