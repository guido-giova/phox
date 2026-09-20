package parser;

import utils.Chars;

import java.util.List;

public class Scanner {
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
            
            if (cc == '/' && this.index + 1 < this.length && text.charAt(this.index + 1) == '*') {
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
            
            this.index++;
        }
        
        this.tokenizeAndAddToList(tokens, lastIndex, this.length);
        return tokens;
    }
    
    private Token scanString() {
        StringBuilder sb = new StringBuilder();
        final int start = this.index;
        this.index++; // skip opening quote
        
        while (this.index < this.length) {
            char cur = this.text.charAt(this.index);
            
            if (cur == '\\') {
                sb.append(this.scanEscapedCharacter());
                continue;
            }
            
            if (cur == '\"') {
                this.index++;
                return new Token.StringLiteral(start, this.text.substring(start + 1, this.index - 1), sb.toString());
            }
            
            sb.append(cur);
            this.index++;
        }
        
        throw new IllegalArgumentException("Unterminated string starting at " + this.index);
    }
    
    private Token scanChar() {
        final int start = this.index;
        this.index++; // skip opening quote
        
        if (this.index + 1 >= this.length) {
            throw new IllegalArgumentException("Character literal never closed at " + this.index);
        }
        
        char cc = text.charAt(this.index);
        if (cc == '\'') {
            throw new IllegalArgumentException("Empty character literal at " + this.index);
        }
        
        if (cc == '\\') {
            cc = this.scanEscapedCharacter();
        } else {
            this.index++;
        }
        
        if (this.index >= this.length) {
            throw new IllegalArgumentException("Character literal never closed at " + this.index);
        }
        
        char next = text.charAt(this.index);
        if (next != '\'') {
            throw new IllegalArgumentException("Too many characters in character literal at " + this.index);
        }
        
        this.index++;
        return new Token.CharacterLiteral(start, this.text.substring(start + 1, this.index - 1), cc);
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
        final int start = this.index;
        this.index += 2; // skip opening "/*"
        
        while (this.index < this.length - 1) {
            if (this.text.charAt(this.index) == '*' && text.charAt(this.index + 1) == '/') {
                this.index += 2;
                return new Token.Comment(start, sb.toString());
            }
            sb.append(this.text.charAt(this.index));
            this.index++;
        }
        
        throw new IllegalArgumentException("Unterminated comment starting at " + this.index);
    }
    
    private Token scanNumber() {
        NumberScanner.NumberScannerResponse response = NumberScanner.scanNumber(this.text, this.index);
        this.index = response.endIndex();
        return response.token();
    }
}
