package utn.back.mordiscoapi.demo;

public final class DemoSeedConflictException extends IllegalStateException {
    public DemoSeedConflictException(String message) {
        super("Demo seed conflict: " + message);
    }
}
