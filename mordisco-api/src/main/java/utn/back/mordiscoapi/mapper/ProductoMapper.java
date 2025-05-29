package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.entity.Producto;

@UtilityClass
public class ProductoMapper {

    public static ProductoRequestDTO toRequestDto(Producto producto) {
        return new ProductoRequestDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getDisponible(),
                ImagenMapper.toDTO(producto.getImagen())
        );
    }
}
