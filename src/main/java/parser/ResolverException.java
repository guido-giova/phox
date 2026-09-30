package parser;

import java.io.Serial;

/**
 * Exception to indicate an error in resolver of classes
 */
public class ResolverException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1390854336821292042L;
    
    /**
     * Constructs a new resolver exception with the given message.
     *
     * @param message the reason
     */
    public ResolverException(String message) {
        super(message);
    }
}
