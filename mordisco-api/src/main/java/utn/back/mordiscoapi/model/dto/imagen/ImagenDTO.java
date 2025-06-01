package utn.back.mordiscoapi.model.dto.imagen;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ImagenDTO(
        @Positive(message = "El id de la imagen debe ser positivo")
        @Schema(description = "Id de la imagen", example = "5")
        Long id,
        @NotNull(message = "El url de la imagen obligatorio")
        @Size(message = "El url de la imagen no debe superar los 255 caracteres", max = 255)
        @Schema(description = "Url de la imagen", example = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fhoycocino.com.ar%2Fmilanesa-a-caballo%2F&psig=AOvVaw2lGRENvZyQ6bbjrlNW6fQI&ust=1747762345621000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq_5yIsI0DFQAAAAAdAAAAABAE")
        String url,
        @Size(message = "El nombre debe tener como maximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre de la imagen es obligatorio")
        @Schema(description = "Nombre de la imagen", example = "milanesas a caballo")
        String nombre) {
}