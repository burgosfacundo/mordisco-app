package utn.back.mordiscoapi.model.dto.pago;

public record MercadoPagoPreferenceResponse(
        String preferenceId,
        String initPoint,
        String sandboxInitPoint,
        Long pedidoId,
        String checkoutUrl
) {
    public MercadoPagoPreferenceResponse {
        if (checkoutUrl == null || checkoutUrl.isBlank()) {
            throw new IllegalArgumentException("checkoutUrl is required");
        }
    }
}
