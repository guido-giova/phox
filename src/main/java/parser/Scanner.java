package parser;

import java.util.List;

public class Scanner {
    public static List<Token> scan(String text) {
        List<Token> tokens = new java.util.ArrayList<>();
        final int ll = text.length();
        int ii = 0;
        int ps = 0;
        
        while (ii < ll) {
            char c = text.charAt(ii);
            
            if (c == '\"' || c == '\'') {
                tokens.addAll(Lexer.tokenize(text.substring(ps, ii), ps));
                ii = scanString(text, ii, c, tokens);
                ps = ii;
                continue;
            }
            
            if (c == '/' && ii + 1 < ll && text.charAt(ii + 1) == '*') {
                tokens.addAll(Lexer.tokenize(text.substring(ps, ii), ps));
                ii = scanComment(text, ii, tokens);
                ps = ii;
                continue;
            }
            
            
            ii++;
        }
        
        tokens.addAll(Lexer.tokenize(text.substring(ps, ll), ps));
        return tokens;
    }
    
    private static int scanString(String text, int start, char quote, List<Token> tokens) {
        final int ll = text.length();
        StringBuilder sb = new StringBuilder();
        int ii = start + 1; // skip opening quote
        
        while (ii < ll) {
            char cur = text.charAt(ii);
            
            if (cur == '\\') {
                if (ii + 1 >= ll) {
                    throw new ValidationException("Trailing escape character", ii);
                }
                char next = text.charAt(ii + 1);
                switch (next) {
                    case 'n'  -> { sb.append('\n'); ii += 2; }
                    case 't'  -> { sb.append('\t'); ii += 2; }
                    case 'r'  -> { sb.append('\r'); ii += 2; }
                    case '\\' -> { sb.append('\\'); ii += 2; }
                    case '"'  -> { sb.append('\"'); ii += 2; }
                    case '\'' -> { sb.append('\''); ii += 2; }
                    case 'u'  -> {
                        if (ii + 6 > ll) {
                            throw new ValidationException("Invalid Unicode escape", ii);
                        }
                        String hex = text.substring(ii + 2, ii + 6);
                        sb.append((char) Integer.parseInt(hex, 16));
                        ii += 6;
                    }
                    default -> throw new ValidationException("Unknown escape '\\" + next + "'", ii);
                }
                continue;
            }
            
            if (cur == quote) {
                tokens.add(new Token.StringLiteral(start, sb.toString()));
                return ii + 1;
            }
            
            sb.append(cur);
            ii++;
        }
        
        throw new ValidationException("Unterminated string starting", start);
    }
    
    private static int scanComment(String text, int start, List<Token> tokens) {
        final int ll = text.length();
        StringBuilder sb = new StringBuilder();
        int ii = start + 2; // skip opening "/*"
        
        while (ii < ll - 1) {
            if (text.charAt(ii) == '*' && text.charAt(ii + 1) == '/') {
                tokens.add(new Token.Comment(start, sb.toString()));
                return ii + 2;
            }
            sb.append(text.charAt(ii));
            ii++;
        }
        
        throw new ValidationException("Unterminated comment starting", start);
    }
}
