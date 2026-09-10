package parser;

import java.io.Serial;

/**
 * Exception to indicate an error in validation of a Phox class
 */
public class ValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3532412369150737442L;
    
    /**
     * Constructs a new validation exception with the given message.
     *
     * @param message the reason
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new validation exception with the given message and the given index, with the format:
     * "[<b>message</b>] at [<b>at</b>]"
     *
     * @param message the reason
     * @param at      the index of the error
     */
    public ValidationException(String message, int at) {
        this(String.format("%s at %d", message, at));
    }
}
