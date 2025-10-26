package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.promocion.PromocionRequestDTO;
import utn.back.mordiscoapi.model.dto.promocion.PromocionResponseDTO;
import utn.back.mordiscoapi.model.entity.Promocion;
import utn.back.mordiscoapi.model.entity.Restaurante;
import utn.back.mordiscoapi.utils.Sanitize;

@UtilityClass // Anotación de lombok para indicar que esta clase es una clase de utilidad
public class PromocionMapper {

    /**
     * Convierte un DTO de promoción a una entidad de promoción.
     * @param dto el DTO de promoción a convertir
     * @return la entidad de promoción con los datos del DTO
     */
    public static Promocion toEntity(PromocionRequestDTO dto) {
        Restaurante restaurante = Restaurante.builder()
                .id(dto.restauranteId())
                .build();
        return Promocion.builder()
                .descripcion(Sanitize.trimToNull(dto.descripcion()))
                .descuento(dto.descuento())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .restaurante(restaurante)
                .build();
    }
    public static PromocionResponseDTO toDTO (Promocion promocion){
        return new PromocionResponseDTO(promocion.getId(),
                promocion.getDescripcion(),
                promocion.getDescuento(),
                promocion.getFechaInicio(),
                promocion.getFechaFin());
    }
}
