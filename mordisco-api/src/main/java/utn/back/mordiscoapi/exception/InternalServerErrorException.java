package utn.back.mordiscoapi.exception;

public class InternalServerErrorException extends Exception{
    public InternalServerErrorException(String message) {
        super(message);
    }
}
