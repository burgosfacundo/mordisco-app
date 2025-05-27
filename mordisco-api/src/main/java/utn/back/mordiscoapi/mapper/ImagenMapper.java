package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
@UtilityClass
public class ImagenMapper {
    public static ImagenResponseDTO toDTO(Imagen imagen){
        return new ImagenResponseDTO(imagen.getId(), imagen.getUrl(),imagen.getNombre());
    }
}
