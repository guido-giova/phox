package parser.exception;

import java.io.Serial;

/**
 * Runtime exception (unchecked) for general IO failures or interruptions.
 */
public class PhoxIOException extends PhoxCompilationException {
    @Serial
    private static final long serialVersionUID = -405780014323945983L;
    
    /**
     * Constructs an io exception with the given message.
     *
     * @param message the reason for the exception
     */
    public PhoxIOException(String message) {
        super(message);
    }
    
    /**
     * Constructs an io exception with the given cause.
     *
     * @param cause of the exception being thrown
     */
    public PhoxIOException(Throwable cause) {
        super(cause);
    }
}
