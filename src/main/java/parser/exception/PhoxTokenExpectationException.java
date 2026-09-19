package parser.exception;

import parser.Token;

import java.io.Serial;

/**
 * Runtime exception (unchecked) for when expected tokens are not present.
 */
public class PhoxTokenExpectationException extends PhoxCompilationException {
    @Serial
    private static final long serialVersionUID = 1701233553012842244L;
    
    /**
     * Constructs a token expectation exception with the message "Expected [expected] at [actual.start]"
     *
     * @param expected token
     * @param actual   The actual token (null -> eof)
     */
    public PhoxTokenExpectationException(Token expected, Token actual) {
        super(PhoxTokenExpectationException.formatExpectedAtMessage(expected, actual));
    }
    
    /**
     * Constructs a token expectation exception with the message "Expected [expected] at EOF"
     *
     * @param expected token
     */
    public PhoxTokenExpectationException(Token expected) {
        super(PhoxTokenExpectationException.formatExpectedEOFMessage(expected));
    }
    
    private static String formatExpectedAtMessage(Token expected, Token actual) {
        if (actual == null) {
            return PhoxTokenExpectationException.formatExpectedEOFMessage(expected);
        }
        return String.format("Expected %s at %d", expected, actual.start());
    }
    
    private static String formatExpectedEOFMessage(Token expected) {
        return String.format("Expected %s at EOF", expected);
    }
}
