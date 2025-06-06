package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.entity.*;

import java.util.List;
@UtilityClass
public class PedidoMapper {
    /**
     * Convierte un DTORequest de pedido a una entidad de pedido.
     * @param dto el DTORequest de pedido a convertir
     * @return la entidad de pedido con los datos del DTORequest
     */
    public static Pedido toEntity(PedidoDTORequest dto) {
        Usuario cliente = Usuario.builder()
                .id(dto.idCliente()).build();

        Restaurante restaurante = Restaurante.builder()
                .id(dto.idRestaurante()).build();

        Direccion direccion = Direccion.builder()
                .id(dto.idDireccion()).build();

        List<ProductoPedido> lista = dto.productos().stream().map(productoPedidoDTO -> {
            Producto producto = Producto.builder()
                    .id(productoPedidoDTO.producto_id())
                    .build();

            return ProductoPedido.builder()
                    .producto(producto)
                    .cantidad(productoPedidoDTO.cantidad())
                    .build();
        }).toList();

        return Pedido.builder()
                .cliente(cliente)
                .restaurante(restaurante)
                .direccionEntrega(direccion)
                .tipoEntrega(dto.tipoEntrega())
                .items(lista)
                .build();
    }

}
