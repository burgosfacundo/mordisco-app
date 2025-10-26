package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.direccion.DireccionCreateDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionResponseDTO;
import utn.back.mordiscoapi.model.dto.direccion.DireccionUpdateDTO;
import utn.back.mordiscoapi.model.entity.Direccion;
import utn.back.mordiscoapi.utils.Sanitize;


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
     * Convierte una entidad de dirección a un DTO de dirección.
     * @param dto la entidad de dirección a convertir
     * @return el DTO de dirección con los datos de la entidad
     */
    public Direccion fromCreateDTO(DireccionCreateDTO dto) {
        return Direccion.builder()
                .calle(Sanitize.collapseSpaces(dto.calle()))
                .numero(Sanitize.trimToNull(dto.numero()))
                .piso(Sanitize.trimToNull(dto.piso()))
                .depto(Sanitize.trimToNull(dto.depto()))
                .codigoPostal(Sanitize.trimToNull(dto.codigoPostal()))
                .ciudad(Sanitize.collapseSpaces(dto.ciudad()))
                .referencias(Sanitize.trimToNull(dto.referencias()))
                .latitud(dto.latitud())
                .longitud(dto.longitud())
                .build();
    }

    /**
     * Aplica un UpdateDTO sobre una entidad ya manejada por JPA (managed).
     * @param dto a aplicar sobre la entidad
     * @param target entidad manejada por JPA
     */
    public void applyUpdate(DireccionUpdateDTO dto, Direccion target) {
        target.setCalle(Sanitize.collapseSpaces(dto.calle()));
        target.setNumero(Sanitize.trimToNull(dto.numero()));
        target.setPiso(Sanitize.trimToNull(dto.piso()));
        target.setDepto(Sanitize.trimToNull(dto.depto()));
        target.setCodigoPostal(Sanitize.trimToNull(dto.codigoPostal()));
        target.setCiudad(Sanitize.collapseSpaces(dto.ciudad()));
        target.setReferencias(Sanitize.trimToNull(dto.referencias()));
        target.setLatitud(dto.latitud());
        target.setLongitud(dto.longitud());
    }
}
