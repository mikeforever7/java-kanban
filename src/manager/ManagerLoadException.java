package manager;

public class ManagerLoadException extends RuntimeException {
    String message;

    public ManagerLoadException(String message) {
        super(message);
    }
}
