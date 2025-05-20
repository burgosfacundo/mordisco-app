package utn.back.mordiscoapi.model.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Data Transfer Object(DTO) para la entidad Promoción
public record ImagenDTO(
        // Anotación de Spring Validation que valida que no sea nulo para el campo descuento
        @NotNull(message = "El url de la imagen obligatorio")
        @Schema(description = "Url de la imagen", example = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fhoycocino.com.ar%2Fmilanesa-a-caballo%2F&psig=AOvVaw2lGRENvZyQ6bbjrlNW6fQI&ust=1747762345621000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq_5yIsI0DFQAAAAAdAAAAABAE")
        String url,
        // Anotación de Spring Validation que valida tamaño para el campo descripción
        @Size(message = "El nombre debe tener como maximo 50 caracteres", max = 50)
        @NotNull(message = "El nombre de la imagen es obligatorio")
        // Anotación para documentar un campo de ejemplo en Swagger
        @Schema(description = "Nombre de la imagen", example = "milanesas a caballo")
        String nombre
        ) {
}