package parser.exception;

import java.io.Serial;

/**
 * Runtime exception (unchecked) for when a number interpretation fails.
 */
public class PhoxNumberInterpretationException extends PhoxValidationException {
    @Serial
    private static final long serialVersionUID = -8399634482776253914L;
    
    /**
     * Constructs a new number interpretation exception with the given message.
     *
     * @param message the reason
     */
    public PhoxNumberInterpretationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new number interpretation exception with the given message and the given index, with the format:
     * "[<b>message</b>] at [<b>at</b>]"
     *
     * @param message the reason
     * @param at      the index of the error
     */
    public PhoxNumberInterpretationException(String message, int at) {
        super(message, at);
    }
}
