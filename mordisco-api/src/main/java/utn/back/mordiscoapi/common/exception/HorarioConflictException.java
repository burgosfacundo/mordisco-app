package utn.back.mordiscoapi.common.exception;

public class HorarioConflictException extends BadRequestException {
    public HorarioConflictException(String message) {
        super(message);
    }
}