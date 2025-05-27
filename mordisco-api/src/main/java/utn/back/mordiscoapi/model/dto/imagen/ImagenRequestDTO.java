package utn.back.mordiscoapi.model.dto.imagen;

import io.swagger.v3.oas.annotations.media.Schema;

public record ImagenRequestDTO(
        @Schema(description = "Url de la imagen", example = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fhoycocino.com.ar%2Fmilanesa-a-caballo%2F&psig=AOvVaw2lGRENvZyQ6bbjrlNW6fQI&ust=1747762345621000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq_5yIsI0DFQAAAAAdAAAAABAE")
        String url,
        @Schema(description = "Nombre de la imagen", example = "milanesas a caballo")
        String nombre
) {
}
