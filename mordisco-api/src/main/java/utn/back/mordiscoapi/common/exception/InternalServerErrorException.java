package utn.back.mordiscoapi.common.exception;

public class InternalServerErrorException extends Exception{
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
