package parser;

import java.util.List;
import java.util.Optional;

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
        INT64,
        FLOAT32,
        FLOAT64,
        INT32
    }
    
    enum FlowTypeKind implements KeywordKind {
    
    }
    
    enum ModifierTypeKind implements KeywordKind {
    
    }
    
    enum TypeTypeKind implements KeywordKind {
    
    }
    
    class Mapper {
        private static final List<KeywordKind> KEYWORD_KIND_LIST;
        private static final java.util.Map<Character, SymbolKind> BY_CHAR;
        
        static {
            KEYWORD_KIND_LIST = new java.util.ArrayList<>();
            KEYWORD_KIND_LIST.addAll(List.of(DataTypeKind.values()));
            KEYWORD_KIND_LIST.addAll(List.of(FlowTypeKind.values()));
            KEYWORD_KIND_LIST.addAll(List.of(ModifierTypeKind.values()));
            KEYWORD_KIND_LIST.addAll(List.of(TypeTypeKind.values()));
            
            BY_CHAR = java.util.Arrays.stream(SymbolKind.values())
                                      .collect(java.util.stream.Collectors.toMap(k -> k.ch, k -> k));
        }
        
        public static Optional<Token> getToken(char chr, int start) {
            return java.util.Optional.ofNullable(BY_CHAR.get(chr))
                                     .map(kind -> new Symbol(start, kind));
        }
    }
}
