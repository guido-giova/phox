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
    
    private final String text;
    private final int length;
    private int index;
    
    private Scanner(String text) {
        this.text   = text;
        this.length = text.length();
        this.index  = 0;
    }
    
    public static List<Token> scan(String text) {
        return new Scanner(text).scan();
    }
    
    private boolean hasNext() {
        return this.index < this.length;
    }
    
    private void tokenizeAndAddToList(List<Token> tokens, int beginIndex, int endIndex) {
        String substring = this.text.substring(beginIndex, endIndex);
        List<Token> tokenized = Lexer.tokenize(substring, beginIndex);
        tokens.addAll(tokenized);
    }
    
    private List<Token> scan() {
        List<Token> tokens = new java.util.ArrayList<>();
        int lastIndex = 0;
        
        while (this.index < this.length) {
            char cc = this.text.charAt(this.index);
            
            if (cc == '\"' || cc == '\'') {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanString(cc));
                lastIndex = this.index;
                continue;
            }
            
            if (cc == '/' && this.index + 1 < this.length && text.charAt(this.index + 1) == '*') {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanComment());
                lastIndex = this.index;
                continue;
            }
            
            // todo: refactor
            boolean atIdentifierBoundary = (this.index == 0) || !Chars.isIdentifierChar(text.charAt(this.index - 1));
            if (Chars.isDecDigit(cc) && atIdentifierBoundary) {
                tokens.addAll(Lexer.tokenize(text.substring(lastIndex, this.index), lastIndex));
                this.index = Scanner.scanNumber(text, this.index, tokens);
                lastIndex = this.index;
            }
            this.index++;
        }
        
        this.tokenizeAndAddToList(tokens, lastIndex, this.length);
        return tokens;
    }
    
    private Token scanString(char quote) {
        StringBuilder sb = new StringBuilder();
        this.index++; // skip opening quote
        
        while (this.index < this.length) {
            char cur = this.text.charAt(this.index);
            
            if (cur == '\\') {
                sb.append(this.scanEscapedCharacter());
                continue;
            }
            
            if (cur == quote) {
                this.index++;
                return new Token.StringLiteral(this.index - 1, sb.toString());
            }
            
            sb.append(cur);
            this.index++;
        }
        
        throw new IllegalArgumentException("Unterminated string starting at " + this.index);
    }
    
    private char scanEscapedCharacter() {
        if (this.index + 1 >= this.length) {
            throw new IllegalArgumentException("Trailing escape character at " + this.index);
        }
        char next = this.text.charAt(this.index + 1);
        char escaped;
        switch (next) {
            case 'n'  -> { this.index += 2; escaped = '\n'; }
            case 't'  -> { this.index += 2; escaped = '\t'; }
            case 'r'  -> { this.index += 2; escaped = '\r'; }
            case '\\' -> { this.index += 2; escaped = '\\'; }
            case '"'  -> { this.index += 2; escaped = '\"'; }
            case '\'' -> { this.index += 2; escaped = '\''; }
            case 'u'  -> {
                if (this.index + 6 > this.length) {
                    throw new IllegalArgumentException("Invalid Unicode escape at " + this.index);
                }
                String hex = this.text.substring(this.index + 2, this.index + 6);
                this.index += 6;
                escaped = (char) Integer.parseInt(hex, 16);
            }
            default -> throw new IllegalArgumentException("Unknown escape '\\" + next + "' at " + this.index);
        }
        return escaped;
    }
    
    private Token scanComment() {
        StringBuilder sb = new StringBuilder();
        this.index++; // skip opening "/*"
        
        while (this.index < this.length - 1) {
            if (this.text.charAt(this.index) == '*' && text.charAt(this.index + 1) == '/') {
                this.index += 2;
                return new Token.Comment(this.index - 2, sb.toString());
            }
            sb.append(this.text.charAt(this.index));
            this.index++;
        }
        
        throw new IllegalArgumentException("Unterminated comment starting at " + this.index);
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
        ii.addAndGet(2); // skip 0b
        Scanner.consumeDigitRun(text, ii, Chars::isBinDigit);
        
    }
    
    private static void octalInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
        ii.addAndGet(2); // skip 0o
        Scanner.consumeDigitRun(text, ii, Chars::isOctDigit);
        
    }
    
    private static void hexadecimalInterpreter(final String text, final int ll, final AtomicInteger ii, final AtomicBoolean hasDot, final AtomicBoolean hasExponent) {
        ii.addAndGet(2); // skip 0x
        Scanner.consumeDigitRun(text, ii, Chars::isHexDigit);
        
    }
}
