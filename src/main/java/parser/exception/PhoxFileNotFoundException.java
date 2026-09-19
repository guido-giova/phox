package parser.exception;

import java.io.Serial;

/**
 * Runtime exception (unchecked) for when the file requested is not found.
 */
public class PhoxFileNotFoundException extends PhoxCompilationException {
    @Serial
    private static final long serialVersionUID = -8399634482776253914L;
    
    /**
     * Constructs a file not found exception with the given message.
     *
     * @param message of the exception
     */
    public PhoxFileNotFoundException(String message) {
        super(message);
    }
}
