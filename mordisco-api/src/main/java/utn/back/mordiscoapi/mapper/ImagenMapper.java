package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.imagen.ImagenDTO;
import utn.back.mordiscoapi.model.entity.Imagen;

@UtilityClass
public class ImagenMapper {
    public static ImagenDTO toDTO(Imagen imagen) {
        return new ImagenDTO(
                imagen.getId(),
                imagen.getUrl(),
                imagen.getNombre()
        );
    }
}
