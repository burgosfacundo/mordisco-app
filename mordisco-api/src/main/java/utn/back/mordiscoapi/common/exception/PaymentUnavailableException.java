package utn.back.mordiscoapi.common.exception;

public class PaymentUnavailableException extends RuntimeException {
    public PaymentUnavailableException() {
        super("Mercado Pago is not configured");
    }

    public PaymentUnavailableException(String message) {
        super(message);
    }
}
