package utn.back.mordiscoapi.mapper;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.DireccionDTO;
import utn.back.mordiscoapi.model.entity.Direccion;

@UtilityClass
public class DireccionMapper {

    public static Direccion toEntity(DireccionDTO direccionDTO){
        return Direccion.builder()
                .calle(direccionDTO.calle())
                .numero(direccionDTO.numero())
                .piso(direccionDTO.piso())
                .depto(direccionDTO.depto())
                .codigoPostal(direccionDTO.codigoPostal())
                .referencias(direccionDTO.referencias())
                .latitud(direccionDTO.latitud())
                .longitud(direccionDTO.longitud())
                .ciudad(direccionDTO.ciudad())
                .build();
    }
}
