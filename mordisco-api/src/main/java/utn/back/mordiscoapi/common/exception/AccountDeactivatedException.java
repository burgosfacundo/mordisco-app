package utn.back.mordiscoapi.common.exception;

import lombok.Getter;

@Getter
public class AccountDeactivatedException extends Exception {
    private final String motivo;

    public AccountDeactivatedException(String motivo) {
        super("La cuenta ha sido desactivada. Motivo: " + motivo);
        this.motivo = motivo;
    }
}
