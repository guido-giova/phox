package parser.exception;

import java.io.Serial;

/**
 * Exception to indicate an error in the validation of a number
 */
public class PhoxNumberValidationException extends PhoxValidationException {
    @Serial
    private static final long serialVersionUID = 3847092540484941407L;
    
    /**
     * Constructs a new number validation exception with the given message.
     *
     * @param message the reason
     */
    public PhoxNumberValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new number validation exception with the given message and the given index, with the format:
     * "[<b>message</b>] at [<b>at</b>]"
     *
     * @param message the reason
     * @param at      the index of the error
     */
    public PhoxNumberValidationException(String message, int at) {
        super(message, at);
    }
}
