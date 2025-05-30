package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.promocion.PromocionDTO;
import utn.back.mordiscoapi.model.entity.Promocion;

@UtilityClass // Anotación de lombok para indicar que esta clase es una clase de utilidad
public class PromocionMapper {

    /**
     * Convierte un DTO de promoción a una entidad de promoción.
     * @param dto el DTO de promoción a convertir
     * @return la entidad de promoción con los datos del DTO
     */
    public static Promocion toEntity(PromocionDTO dto) {
        return Promocion.builder()
                .descripcion(dto.descripcion())
                .descuento(dto.descuento())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .build();
    }
}
