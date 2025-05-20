package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.PedidoDTORequest;
import utn.back.mordiscoapi.model.dto.PedidoDTOResponse;
import utn.back.mordiscoapi.model.entity.Pedido;
import utn.back.mordiscoapi.model.entity.Usuario;

@UtilityClass
public class PedidoMapper {
    public static Pedido toEntity (PedidoDTORequest pedidoDTORequest){
        Usuario cliente=new Usuario ();
        cliente.setId(pedidoDTORequest.idCliente());
        Restaurante restaurante=new Restaurante ();
        restaurante.setId(pedidoDTORequest.idRestaurante());
        ProductoPedido productoPedido=new ProductoPedido();
        productoPedido.setId(pedidoDTORequest.productoPedido);
        return Pedido.builder()
                .cliente(cliente)
                .restaurante(restaurante)
                .direccionEntrega(pedidoDTORequest.direccionEntrega())
                .tipoEntrega(pedidoDTORequest.tipoEntrega())
                .tipoPago(pedidoDTORequest.tipoPago())
                .fechaHora(pedidoDTORequest.fechaHora())
                .estado(pedidoDTORequest.estado())
                .productoPedido(pedidoDTORequest.productoPedido)
                .build();
    }
}
