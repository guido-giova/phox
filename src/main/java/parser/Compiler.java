package parser;

import java.util.List;

/**
 * Utility class for determining if a String is a valid program in the Phox language.
 */
public final class Compiler {
    private Compiler() {
        throw new UnsupportedOperationException("Don't instantiate Compiler");
    }
    
    /**
     * Validates if the given String is valid.
     *
     * @param text that will be validated
     * @return Failure with reason if it's invalid or Success with tokens if valid.
     * @see CompilerResponse
     */
    public static CompilerResponse validate(String text) {
        try {
            List<Token> tokens;
            
            tokens = Scanner.scan(text);
            return new CompilerResponse.Success(tokens);
        } catch (IllegalArgumentException iae) {
            return new CompilerResponse.Failed(iae.getLocalizedMessage());
        }
    }
}

