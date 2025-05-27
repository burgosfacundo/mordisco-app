package utn.back.mordiscoapi.mapper;

import jakarta.persistence.*;
import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.enums.EstadoPedido;
import utn.back.mordiscoapi.enums.TipoEntrega;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTORequest;
import utn.back.mordiscoapi.model.dto.pedido.PedidoDTOResponse;
import utn.back.mordiscoapi.model.dto.promocion.PromocionDTO;
import utn.back.mordiscoapi.model.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        List<ProductoPedido> lista = dto.productos().stream().map(productoPedidoDTO ->
                ProductoPedido.builder()
                        .id(productoPedidoDTO.producto_id())
                        .build()).toList();
        return Pedido.builder()
                .cliente(cliente)
                .restaurante(restaurante)
                .direccionEntrega(direccion)
                .tipoEntrega(dto.tipoEntrega())
                .items(lista)
                .build();
    }

}
