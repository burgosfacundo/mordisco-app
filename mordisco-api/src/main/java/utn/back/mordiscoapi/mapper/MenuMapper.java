package utn.back.mordiscoapi.mapper;

import lombok.experimental.UtilityClass;
import utn.back.mordiscoapi.model.dto.menu.MenuDTO;
import utn.back.mordiscoapi.model.entity.Menu;

@UtilityClass
public class MenuMapper {

    public static MenuDTO toDto(Menu menu) {
        return new MenuDTO(
                menu.getId(),
                menu.getNombre(),
                menu.getProductos().stream()
                        .map(ProductoMapper::toRequestDto)
                        .toList()
        );
    }
}
