package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;
import utn.back.mordiscoapi.model.dto.imagen.ImagenUpdateDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
import utn.back.mordiscoapi.utils.Sanitize;

@UtilityClass
public class ImagenMapper {
    /**
     * Convierte una entidad de imagen a un DTO de imagen.
     * @param imagen la entidad de imagen a convertir
     * @return el DTO de imagen con los datos de la entidad
     */
    public static ImagenResponseDTO toDTO(Imagen imagen){
        return new ImagenResponseDTO(imagen.getId(),
                imagen.getUrl(),
                imagen.getNombre());
    }

    /**
     * Convierte un DTO update de imagen a una entidad de imagen.
     * @param dto el DTO de imagen a convertir
     */
    public static void applyUpdate(ImagenUpdateDTO dto, Imagen target) {
        target.setUrl(Sanitize.trimToNull(dto.url()));
        target.setNombre(Sanitize.collapseSpaces(dto.nombre()));
    }
}
