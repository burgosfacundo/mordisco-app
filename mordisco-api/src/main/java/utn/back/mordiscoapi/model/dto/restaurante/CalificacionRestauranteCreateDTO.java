package utn.back.mordiscoapi.model.dto.restaurante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CalificacionRestauranteCreateDTO(
        @NotNull(message = "El id del restaurante no debe ser nulo")
        @Positive(message = "El id del restaurante debe ser positivo")
        @Schema(description = "El id del restaurante calificado ", example = "10")
        Long idRestaurante,
        @NotNull(message = "El id del usuario no debe ser nulo")
        @Positive(message = "El id del usuario debe ser positivo")
        @Schema(description = "El id del usuario que califica ", example = "3")
        Long idUsuario,
        @NotNull(message = "Se debe ingresar un puntaje no nulo")
        @Min(value = 0, message = "El puntaje no puede ser menor a 0")
        @Max(value = 5, message = "El puntaje no puede ser mayor a 5")
        @Schema(description = "Puntaje", example = "4")
        Integer puntaje,
        @Size(message = "El comentario no debe superar los 255 caracteres", max = 255)
        @Schema(description = "El comentario de la calificación",example = "Muy rico el doble cuarto de libra")
        String comentario
){

}
