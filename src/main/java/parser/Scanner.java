package parser;

import utils.Chars;

import java.util.List;

public final class Scanner extends TextScanner {
    private Scanner(String text) {
        super(text, 0);
    }
    
    public static List<Token> scan(String text) {
        return new Scanner(text).scan();
    }
    
    private void tokenizeAndAddToList(List<Token> tokens, int beginIndex, int endIndex) {
        String substring = this.substring(beginIndex, endIndex);
        List<Token> tokenized = Lexer.tokenize(substring, beginIndex);
        tokens.addAll(tokenized);
    }
    
    private List<Token> scan() {
        List<Token> tokens = new java.util.ArrayList<>();
        int lastIndex = 0;
        
        while (this.hasCurrent()) {
            char cc = this.getCurrent();
            
            if (cc == '\"') {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanString());
                lastIndex = this.index;
                continue;
            }
            
            if (cc == '\'') {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanChar());
                lastIndex = this.index;
                continue;
            }
            
            if (cc == '/' && this.hasRun(2) && text.charAt(this.index + 1) == '*') {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanComment());
                lastIndex = this.index;
                continue;
            }
            
            boolean atIdentifierBoundary = (this.index == 0) || !Chars.isIdentifierChar(text.charAt(this.index - 1));
            if (Chars.isDecDigit(cc) && atIdentifierBoundary) {
                this.tokenizeAndAddToList(tokens, lastIndex, this.index);
                tokens.add(this.scanNumber());
                lastIndex = this.index;
                continue;
            }
            
            this.consume();
        }
        
        this.tokenizeAndAddToList(tokens, lastIndex, this.length);
        return tokens;
    }
    
    private Token scanString() {
        StringBuilder sb = new StringBuilder();
        final int start = this.consume(); // skip opening quote
        
        while (this.hasCurrent()) {
            char cur = this.getCurrent();
            
            if (cur == '\\') {
                sb.append(this.scanEscapedCharacter());
                continue;
            }
            
            if (cur == '\"') {
                this.consume();
                return new Token.StringLiteral(start, this.substring(start + 1, this.index - 1), sb.toString());
            }
            
            sb.append(cur);
            this.consume();
        }
        
        throw new IllegalArgumentException("Unterminated string starting at " + this.index);
    }
    
    private Token scanChar() {
        final int start = this.consume(); // skip opening quote
        
        if (! this.hasRun(2)) {
            throw new IllegalArgumentException("Character literal never closed at " + this.index);
        }
        
        char cc = this.getCurrent();
        if (cc == '\'') {
            throw new IllegalArgumentException("Empty character literal at " + this.index);
        }
        
        if (cc == '\\') {
            cc = this.scanEscapedCharacter();
        } else {
            this.consume();
        }
        
        if (! this.hasRun(1)) {
            throw new IllegalArgumentException("Character literal never closed at " + this.index);
        }
        
        char next = this.getCurrent();
        if (next != '\'') {
            throw new IllegalArgumentException("Too many characters in character literal at " + this.index);
        }
        
        this.consume();
        return new Token.CharacterLiteral(start, this.substring(start + 1, this.index - 1), cc);
    }
    
    private char scanEscapedCharacter() {
        if (! this.hasRun(2)) {
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
                if (! this.hasRun(6)) {
                    throw new IllegalArgumentException("Invalid Unicode escape at " + this.index);
                }
                String hex = this.substring(this.index + 2, this.index + 6);
                this.index += 6;
                escaped = (char) Integer.parseInt(hex, 16);
            }
            default -> throw new IllegalArgumentException("Unknown escape '\\" + next + "' at " + this.index);
        }
        return escaped;
    }
    
    private Token scanComment() {
        StringBuilder sb = new StringBuilder();
        final int start = this.consume(2); // skip opening "/*"
        
        while (this.hasRun(2)) {
            if (this.getCurrent() == '*' && text.charAt(this.index + 1) == '/') {
                this.consume(2);
                return new Token.Comment(start, sb.toString());
            }
            sb.append(this.getCurrent());
            this.consume();
        }
        
        throw new IllegalArgumentException("Unterminated comment starting at " + this.index);
    }
    
    private Token scanNumber() {
        NumberScanner.NumberScannerResponse response = NumberScanner.scanNumber(this.text, this.index);
        this.index = response.endIndex();
        return response.token();
    }
}
