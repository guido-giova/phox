package parser.exception;

import java.io.Serial;

/**
 * Parent class for all exceptions raised when compiling Phox code
 */
public class PhoxCompilationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3452621655957814386L;
    
    /**
     * Constructs a compilation exception with the given message.
     *
     * @param message the reason for the exception
     */
    public PhoxCompilationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a compilation exception with the given cause.
     *
     * @param cause of the exception being thrown
     */
    public PhoxCompilationException(Throwable cause) {
        super(cause);
    }
}
