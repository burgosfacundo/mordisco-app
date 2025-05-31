package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;

import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseRestauranteDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
@UtilityClass
public class ImagenMapper {
    public static ImagenResponseRestauranteDTO toDTO(Imagen imagen){
        return new ImagenResponseRestauranteDTO(imagen.getId());
    }
}
