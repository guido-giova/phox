package parser;

public sealed interface Token {
    /**
     * Determines where the token starts
     *
     * @return index where the token begins
     */
    int start();
    
    record Word(int start, String str) implements Token {}
    record NumberLiteral(int start, String str) implements Token {}
    record Whitespace(int start, String str) implements Token {}
    record StringLiteral(int start, String str) implements Token {}
    record Comment(int start, String str) implements Token {}
    record Keyword(int start, KeywordKind str) implements Token {}
    record Symbol(int start, SymbolKind kind) implements Token {
        char symbol() { return kind.ch; }
    }
    enum SymbolKind {
        AMPERSAND('&'),
        ASTERISK('*'),
        AT('@'),
        BACK_SLASH('\\'),
        BRACKET_CLOSE(')'),
        BRACKET_OPEN('('),
        COMA(','),
        COLON(':'),
        CURLY_BRACKET_CLOSE('}'),
        CURLY_BRACKET_OPEN('{'),
        DOUBLE_QUOTATION_MARK('"'),
        EQUALS('='),
        EXCLAMATION_MARK('!'),
        FORWARD_SLASH('/'),
        LESS_THAN('<'),
        MINUS('-'),
        MORE_THAN('>'),
        PERIOD('.'),
        PIPE('|'),
        PLUS('+'),
        QUESTION_MARK('?'),
        SEMICOLON(';'),
        SIMPLE_QUOTATION_MARK('\''),
        SQUARE_BRACKET_CLOSE(']'),
        SQUARE_BRACKET_OPEN('[')
        ;
        final char ch;
        SymbolKind(char ch) { this.ch = ch; }
    }
    
    sealed interface KeywordKind permits DataTypeKind, FlowTypeKind, ModifierTypeKind, TypeTypeKind {
    }
    
    enum DataTypeKind implements KeywordKind {
    }
    
    enum FlowTypeKind implements KeywordKind {
    
    }
    
    enum ModifierTypeKind implements KeywordKind {
    
    }
    
    enum TypeTypeKind implements KeywordKind {
    
    }
}
