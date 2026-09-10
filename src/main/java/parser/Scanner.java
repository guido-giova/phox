package parser;

import utils.CharPredicate;
import utils.Chars;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scanner {
    private static final String HEXADECIMAL_PREFIX = "0x";
    private static final String OCTAL_PREFIX = "0o";
    private static final String BINARY_PREFIX = "0b";
    
    private static final Map<String, Token.DataTypeKind> SUFFIXES = Map.of(
            "i32", Token.DataTypeKind.INT32,
            "i64", Token.DataTypeKind.INT64,
            "f32", Token.DataTypeKind.FLOAT32,
            "f64", Token.DataTypeKind.FLOAT64
    );
    
    public static List<Token> scan(String text) {
        List<Token> tokens = new java.util.ArrayList<>();
        final int ll = text.length();
        int ii = 0;
        int ps = 0;
        
        while (ii < ll) {
            char cc = text.charAt(ii);
            
            if (cc == '\"' || cc == '\'') {
                tokens.addAll(Lexer.tokenize(text.substring(ps, ii), ps));
                ii = scanString(text, ii, cc, tokens);
                ps = ii;
                continue;
            }
            
            if (cc == '/' && ii + 1 < ll && text.charAt(ii + 1) == '*') {
                tokens.addAll(Lexer.tokenize(text.substring(ps, ii), ps));
                ii = scanComment(text, ii, tokens);
                ps = ii;
                continue;
            }
            
            boolean atIdentifierBoundary = (ii == 0) || !Chars.isIdentifierChar(text.charAt(ii - 1));
            if (Chars.isDecDigit(cc) && atIdentifierBoundary) {
                tokens.addAll(Lexer.tokenize(text.substring(ps, ii), ps));
                ii = Scanner.scanNumber(text, ii, tokens);
                ps = ii;
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
                    throw new IllegalArgumentException("Trailing escape character at " + ii);
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
                            throw new IllegalArgumentException("Invalid Unicode escape at " + ii);
                        }
                        String hex = text.substring(ii + 2, ii + 6);
                        sb.append((char) Integer.parseInt(hex, 16));
                        ii += 6;
                    }
                    default -> throw new IllegalArgumentException("Unknown escape '\\" + next + "' at " + ii);
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
        
        throw new IllegalArgumentException("Unterminated string starting at " + start);
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
        
        throw new IllegalArgumentException("Unterminated comment starting at " + start);
    }
    
    private static int scanNumber(String text, int start, List<Token> tokens) {
        final int ll = text.length();
        AtomicInteger ii = new AtomicInteger(start);
        AtomicBoolean hasDot = new AtomicBoolean(false);
        AtomicBoolean hasExponent = new AtomicBoolean(false);
        
        if (Chars.matchesIgnoreCase(text, ii, BINARY_PREFIX)) {
            Scanner.binaryInterpreter(text, ll, ii, hasDot, hasExponent);
        } else if (Chars.matchesIgnoreCase(text, ii, OCTAL_PREFIX)) {
            Scanner.octalInterpreter(text, ll, ii, hasDot, hasExponent);
        } else if (Chars.matchesIgnoreCase(text, ii, HEXADECIMAL_PREFIX)) {
            Scanner.hexadecimalInterpreter(text, ll, ii, hasDot, hasExponent);
        } else {
            Scanner.decimalInterpreter(text, ll, ii, hasDot, hasExponent);
        }
        
        Token.DataTypeKind explicitType = null;
        for (Map.Entry<String, Token.DataTypeKind> entry : SUFFIXES.entrySet()) {
            String suf = entry.getKey();
            if (text.regionMatches(ii.get(), suf, 0, suf.length())) {
                int after = ii.get() + suf.length();
                boolean followedByIdentifierChar = after < ll && Chars.isIdentifierChar(text.charAt(after));
                if (! followedByIdentifierChar) {
                    explicitType = entry.getValue();
                    ii.set(after);
                    break;
                }
            }
        }
        
        if (explicitType == Token.DataTypeKind.INT32 || explicitType == Token.DataTypeKind.INT64) {
            if (hasDot.get()) {
                throw new IllegalArgumentException("Integer suffix invalid on non-integer literal at " + start);
            }
        }
        
        Token.DataTypeKind type = explicitType != null
                ? explicitType
                : (hasDot.get() ? Token.DataTypeKind.FLOAT32 : Token.DataTypeKind.INT32);
        
        tokens.add(new Token.NumberLiteral(start, text.substring(start, ii.get())));
        return ii.get();
    }
    
    private static void consumeDigitRun(String text, AtomicInteger ii, CharPredicate isDigit) {
        final int ll = text.length();
        if (ii.get() >= ll || isDigit.negate().test(Chars.charAt(text, ii))) {
            throw new IllegalArgumentException("Expected digit at " + ii);
        }
        ii.incrementAndGet();
        while (ii.get() < ll) {
            char cc = Chars.charAt(text, ii);
            if (isDigit.test(cc)) {
                ii.incrementAndGet();
            } else if (cc == '_' && ii.get() + 1 < ll && isDigit.test(text.charAt(ii.get() + 1))) {
                ii.incrementAndGet(); // skip separator; loop consumes the digit right after it
            } else {
                break;
            }
        }
    }
    
    private static void decimalInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
        Scanner.consumeDigitRun(text, ii, Chars::isDecDigit);
        if (ii.get() < ll && Chars.charAt(text, ii) == '.') {
            hasDot.set(true);
            ii.incrementAndGet();
            Scanner.consumeDigitRun(text, ii, Chars::isDecDigit);
        }
        if (ii.get() < ll && Chars.isExponentSymbol(Chars.charAt(text, ii))) {
            hasExponent.set(true);
            AtomicInteger jj = new AtomicInteger(ii.get() + 1);
            if (jj.get() < ll && Chars.isSignSymbol(Chars.charAt(text, jj))) {
                jj.incrementAndGet();
            }
            if (jj.get() < ll && Chars.isDecDigit(Chars.charAt(text, jj))) {
                Scanner.consumeDigitRun(text, jj, Chars::isDecDigit);
                ii.set(jj.get());
            }
        }
    }
    
    private static void binaryInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
    
    }
    
    private static void octalInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
    
    }
    
    private static void hexadecimalInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
    
    }
}
