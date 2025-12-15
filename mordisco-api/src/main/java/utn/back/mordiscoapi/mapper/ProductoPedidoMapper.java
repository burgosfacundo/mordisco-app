package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.productoPedido.ProductoPedidoResponseDTO;
import utn.back.mordiscoapi.model.entity.Producto;
import utn.back.mordiscoapi.model.entity.ProductoPedido;

@UtilityClass
public class ProductoPedidoMapper {

    /**
     * Convierte un ProductoPedido a un DTOResponse de ProductoPedido.
     * Usa los datos desnormalizados del snapshot para manejar productos eliminados.
     * @param productoPedido el ProductoPedido a convertir
     * @return el DTOResponse de ProductoPedido con los datos del ProductoPedido
     */
    public static ProductoPedidoResponseDTO toDTO(ProductoPedido productoPedido) {
        Long idProducto = productoPedido.getProducto() != null
            ? productoPedido.getProducto().getId()
            : null;

        return new ProductoPedidoResponseDTO(
                productoPedido.getId(),
                productoPedido.getCantidad(),
                productoPedido.getPrecioUnitario(),
                idProducto,
                productoPedido.getNombreProducto() // Usa el snapshot guardado
        );
    }

    /**
     * Crea un snapshot del producto en el ProductoPedido.
     * Copia los datos del producto para preservarlos aunque el producto sea eliminado.
     * @param productoPedido el ProductoPedido a configurar
     * @param producto el Producto del cual crear el snapshot
     */
    public static void crearSnapshotProducto(ProductoPedido productoPedido, Producto producto) {
        productoPedido.setNombreProducto(producto.getNombre());
        productoPedido.setDescripcionProducto(producto.getDescripcion());

        if (producto.getImagen() != null && producto.getImagen().getUrl() != null) {
            productoPedido.setImagenUrl(producto.getImagen().getUrl());
        }
    }
}
