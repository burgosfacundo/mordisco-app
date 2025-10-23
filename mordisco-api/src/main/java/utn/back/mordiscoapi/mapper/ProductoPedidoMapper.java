package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.ProductoPedido;

@UtilityClass
public class ProductoPedidoMapper {

    /**
     * Convierte un ProductoPedido a un DTOResponse de ProductoPedido.
     * @param productoPedido el ProductoPedido a convertir
     * @return el DTOResponse de ProductoPedido con los datos del ProductoPedido
     */
    public static ProductoPedidoResponseDTO toDTO(ProductoPedido productoPedido) {
        return new ProductoPedidoResponseDTO(
                productoPedido.getId(),
                productoPedido.getCantidad(),
                productoPedido.getPrecioUnitario(),
                productoPedido.getProducto().getId(),
                productoPedido.getProducto().getNombre()
        );
    }
}
