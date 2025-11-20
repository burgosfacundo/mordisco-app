package utn.back.mordiscoapi.model.dto.repartidor;

public record RepartidorResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        String email,
        Double latitudActual,
        Double longitudActual
) {}