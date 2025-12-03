package utn.back.mordiscoapi.model.dto.estadisticas;

public record UsuariosTotalesDTO(
        Integer clientes,
        Integer restaurantes,
        Integer repartidores,
        Integer total
) {
}
