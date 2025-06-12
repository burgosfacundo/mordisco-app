package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.producto.ProductoDTO;
import utn.back.mordiscoapi.model.entity.Producto;

@UtilityClass
public class ProductoMapper {

    public static ProductoDTO toDto(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getDisponible(),
                ImagenMapper.toDTO(producto.getImagen())
        );
    }
}
