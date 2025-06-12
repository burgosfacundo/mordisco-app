package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;
import utn.back.mordiscoapi.model.entity.Direccion;

@UtilityClass
public class DireccionMapper {
    /**
     * Convierte una entidad de dirección a un DTO de dirección.
     * @param direccion la entidad de dirección a convertir
     * @return el DTO de dirección con los datos de la entidad
     */
    public static DireccionResponseDTO toDTO (Direccion direccion){
        return new DireccionResponseDTO(direccion.getId(),
                direccion.getCalle(),
                direccion.getNumero(),
                direccion.getPiso(),
                direccion.getDepto(),
                direccion.getCodigoPostal(),
                direccion.getReferencias(),
                direccion.getLatitud(),
                direccion.getLongitud(),
                direccion.getCiudad());
    }

    /**
     * Convierte un DTO de dirección a una entidad de dirección.
     * @param dto el DTO de dirección a convertir
     * @return la entidad de dirección con los datos del DTO
     */
    public static Direccion toEntity(DireccionCreateDTO dto) {
        return Direccion.builder()
                .calle(dto.calle())
                .numero(dto.numero())
                .piso(dto.piso())
                .depto(dto.depto())
                .codigoPostal(dto.codigoPostal())
                .referencias(dto.referencias())
                .latitud(dto.latitud())
                .longitud(dto.longitud())
                .ciudad(dto.ciudad())
                .build();
    }

    /**
     * Convierte un DTO de dirección a una entidad de dirección.
     * @param dto el DTO de dirección a convertir
     * @return la entidad de dirección con los datos del DTO
     */
    public static Direccion updateToEntity(DireccionUpdateDTO dto) {
        return Direccion.builder()
                .id(dto.id())
                .calle(dto.calle())
                .numero(dto.numero())
                .piso(dto.piso())
                .depto(dto.depto())
                .codigoPostal(dto.codigoPostal())
                .referencias(dto.referencias())
                .latitud(dto.latitud())
                .longitud(dto.longitud())
                .ciudad(dto.ciudad())
                .build();
    }
}
