package utn.back.mordiscoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import utn.back.mordiscoapi.model.entity.Usuario;

import java.time.LocalDateTime;

public record CalificacionProductoDTO(
    @NotNull(message = "El producto no debe ser nulo")
    @Schema(description = "Producto ", example = "medialuna dulce")
    ProductoDTO producto,
    @NotNull(message = "El usuario no debe ser nulo")
    @Schema(description = "Usuario ", example = "user12098")
    Usuario usuario,
    @NotNull (message = "Se debe ingresar un puntaje no nulo")
    @Schema(description = "Puntaje", example = "4")
    Integer puntaje,
    @Size(message = "El comentario no debe superar los 100 caracteres", max = 100)
    @NotNull(message = "El comentario no debe ser nulo")
    @Schema(description = "Comentario",example = "Muy rico todo")
    String comentario,
    @NotNull (message = "La fecha no debe ser nula")
    @Schema(description = "Fecha y hora", example = "2025-06-06 14:30")
    LocalDateTime fecha

    ){

}
