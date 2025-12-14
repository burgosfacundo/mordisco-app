package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.producto.ProductoRequestDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseCardDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoResponseDTO;
import utn.back.mordiscoapi.model.dto.producto.ProductoUpdateDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.model.entity.Menu;
import utn.back.mordiscoapi.model.entity.Producto;

@UtilityClass
public class ProductoMapper {

    public static ProductoResponseDTO toDto(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                null, // precioConDescuento - se calculará en el servicio
                null, // porcentajeDescuento - se calculará en el servicio
                false, // tienePromocion - se calculará en el servicio
                null, // descripcionPromocion - se calculará en el servicio
                producto.getDisponible(),
                ImagenMapper.toDTO(producto.getImagen())
        );
    }


    public static ProductoResponseCardDTO toDtoCard(Producto producto) {
        return new ProductoResponseCardDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                null, // precioConDescuento - se calculará en el servicio
                null, // porcentajeDescuento - se calculará en el servicio
                false, // tienePromocion - se calculará en el servicio
                null, // descripcionPromocion - se calculará en el servicio
                producto.getDisponible(),
                ImagenMapper.toDTO(producto.getImagen())
        );
    }


    public static Producto toEntity(Menu menu, ProductoRequestDTO dto) {
        var imagen = ImagenMapper.toEntity(dto.imagen());
        return Producto.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .disponible(dto.disponible())
                .imagen(imagen)
                .precio(dto.precio())
                .menu(menu)
                .build();
    }


    public static void applyUpdate(Producto target, ProductoUpdateDTO dto) {
        target.setNombre(dto.nombre());
        target.setDescripcion(dto.descripcion());
        target.setPrecio(dto.precio());
        target.setDisponible(dto.disponible());
    }
}
