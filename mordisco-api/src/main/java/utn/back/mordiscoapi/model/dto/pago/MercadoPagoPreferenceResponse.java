package utn.back.mordiscoapi.model.dto.pago;

public record MercadoPagoPreferenceResponse(
        String preferenceId,
        String initPoint,
        String sandboxInitPoint,
        Long pedidoId
) {
}
