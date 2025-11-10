package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.menu.MenuResponseDTO;
import utn.back.mordiscoapi.model.entity.Menu;

@UtilityClass
public class MenuMapper {

    public static MenuResponseDTO toDto(Menu menu) {
        return new MenuResponseDTO(
                menu.getId(),
                menu.getNombre()
        );
    }
}
