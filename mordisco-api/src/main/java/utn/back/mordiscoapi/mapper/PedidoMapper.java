package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.pedido.PedidoRequestDTO;
import utn.back.mordiscoapi.model.dto.pedido.PedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.*;

import java.util.List;

@UtilityClass
public class PedidoMapper {
    /**
     * Convierte un DTORequest de pedido a una entidad de pedido.
     * @param dto el DTORequest de pedido a convertir
     * @return la entidad de pedido con los datos del DTORequest
     */
    public static Pedido toEntity(PedidoRequestDTO dto) {
        Usuario cliente = Usuario.builder()
                .id(dto.idCliente()).build();

        Restaurante restaurante = Restaurante.builder()
                .id(dto.idRestaurante()).build();

        Direccion direccion = Direccion.builder()
                .id(dto.idDireccion()).build();

        List<ProductoPedido> lista = dto.productos().stream().map(productoPedidoDTO -> {
            Producto producto = Producto.builder()
                    .id(productoPedidoDTO.productoId())
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

    /**
     * Convierte una entidad de pedido a un DTOResponse de pedido.
     * @param pedido la entidad de pedido a convertir
     * @return el DTOResponse de pedido con los datos de la entidad
     */
    public static PedidoResponseDTO toDTO(Pedido pedido) {
        var usuario = UsuarioMapper.toUsuarioPedidoDTO(pedido.getCliente());
        var restaurante = RestauranteMapper.toRestaurantePedidoDTO(pedido.getRestaurante());
        var productos = pedido.getItems().stream()
                .map(ProductoPedidoMapper::toDTO)
                .toList();
        var direccionEntrega = pedido.getDireccionEntrega() != null
                ? DireccionMapper.toDTO(pedido.getDireccionEntrega())
                : null;

        return new PedidoResponseDTO(pedido.getId(),
                usuario,
                restaurante,
                productos,
                pedido.getTipoEntrega(),
                pedido.getEstado(),
                pedido.getFechaHora(),
                pedido.getTotal(),
                direccionEntrega,
                pedido.getDireccionSnapshot(),
                pedido.getCostoDelivery(),
                pedido.getDistanciaKm(),
                pedido.getSubtotalProductos(),
                pedido.getBajaLogica(),
                pedido.getMotivoBaja(),
                pedido.getFechaBaja(),
                pedido.getEstadoAntesDeCancelado()
                );
    }
}
