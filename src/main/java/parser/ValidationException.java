package parser;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, int at) {
        this(String.format("%s at %d", message, at));
    }
}
