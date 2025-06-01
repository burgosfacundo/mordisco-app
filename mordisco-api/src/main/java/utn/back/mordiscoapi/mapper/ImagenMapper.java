package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;

import utn.back.mordiscoapi.model.dto.imagen.ImagenResponseDTO;
import utn.back.mordiscoapi.model.entity.Imagen;
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
     * Convierte un DTO de imagen a una entidad de imagen.
     * @param dto el DTO de imagen a convertir
     * @return la entidad de imagen con los datos del DTO
     */
    public static Imagen toEntity(ImagenResponseDTO dto) {
        return new Imagen(dto.id(), // ID será generado por la base de datos
                dto.url(),
                dto.nombre());
    }
}
