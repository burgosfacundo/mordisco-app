package utn.back.mordiscoapi.common.exception;

import lombok.Getter;

@Getter
public class AccountDeactivatedException extends Exception {
    private final String motivo;

    public AccountDeactivatedException(String motivo) {
        super("La cuenta ha sido desactivada. Motivo: " + motivo+ ". Para desactivarla por favor envie un mail a mordisco@gmail.com");
        this.motivo = motivo;
    }
}
