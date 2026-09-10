package parser;

import java.util.List;

public final class Compiler {
    private Compiler() {
        throw new UnsupportedOperationException("Don't instantiate Compiler");
    }
    
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

