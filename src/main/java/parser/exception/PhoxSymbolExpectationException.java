package parser.exception;

import parser.Token;

import java.io.Serial;

/**
 * Runtime exception (unchecked) for when expected symbols are not present.
 */
public class PhoxSymbolExpectationException extends PhoxCompilationException {
    @Serial
    private static final long serialVersionUID = 1701233553012842244L;
    
    /**
     * Constructs a symbol expectation exception with the message "Expected '[expected]' symbol at [actual.start]"
     *
     * @param expected symbol
     * @param actual   The actual token (null -> eof)
     */
    public PhoxSymbolExpectationException(Token.SymbolKind expected, Token actual) {
        super(PhoxSymbolExpectationException.formatExpectedAtMessage(expected, actual));
    }
    
    /**
     * Constructs a symbol expectation exception with the message "Expected '[expected]' symbol at EOF"
     *
     * @param expected symbol
     */
    public PhoxSymbolExpectationException(Token.SymbolKind expected) {
        super(PhoxSymbolExpectationException.formatExpectedEOFMessage(expected));
    }
    
    private static String formatExpectedAtMessage(Token.SymbolKind expected, Token actual) {
        if (actual == null) {
            return PhoxSymbolExpectationException.formatExpectedEOFMessage(expected);
        }
        return String.format("Expected '%s' at %d", expected, actual.start());
    }
    
    private static String formatExpectedEOFMessage(Token.SymbolKind expected) {
        return String.format("Expected '%s' at EOF", expected);
    }
}
