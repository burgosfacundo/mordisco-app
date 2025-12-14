package utn.back.mordiscoapi.model.dto.repartidor;

import jakarta.validation.constraints.NotNull;

public record RepartidorDisponibilidadDTO(
        @NotNull(message = "La disponibilidad es obligatoria")
        Boolean disponible,
        Double latitud,
        Double longitud
) {}