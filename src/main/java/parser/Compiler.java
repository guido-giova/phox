package parser;

import java.util.List;

public class Compiler {
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

