package parser;

import java.util.List;

public class Lexer {
    public static List<Token> tokenize(String text) {
        return Lexer.tokenize(text, 0);
    }
    
    public static List<Token> tokenize(String text, int offset) {
        final List<Token> tokens = new java.util.ArrayList<>();
        final int ll = text.length();
        int ii = 0, io;
        StringBuilder sb = null;
        
        while (ii < ll) {
            io = ii + offset;
            char cc = text.charAt(ii);
            
            if (Character.isWhitespace(cc)) {
                if (sb != null) {
                    tokens.add(new Token.Word(offset + ii - sb.length(), sb.toString()));
                    sb = null;
                }
                tokens.add(new Token.Whitespace(io, Character.toString(cc)));
                ii++;
                continue;
            }
            
            if (Character.isLetterOrDigit(cc) || cc == '_') {
                if (sb == null) { sb = new StringBuilder(); }
                sb.append(cc);
                ii++;
                continue;
            }
            
            java.util.Optional<Token> symbolToken = Token.Mapper.getToken(cc, io);
            if (symbolToken.isEmpty()) {
                throw new IllegalArgumentException("Unknown symbol: '" + cc + "' at " + io);
            } else {
                if (sb != null) {
                    tokens.add(new Token.Word(offset + ii - sb.length(), sb.toString()));
                    sb = null;
                }
                tokens.add(symbolToken.get());
                ii++;
            }
        }
        
        if (sb != null) {
            tokens.add(new Token.Word(offset + ii - sb.length(), sb.toString()));
        }
        return tokens;
    }
}
