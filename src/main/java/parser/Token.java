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
    
    /**
     * Represents any group of alphanumeric symbols that isn't a keyword. This represents class names, variable names,
     * method names, etc.
     *
     * @param start Beginning index in the class definition
     * @param str   Value itself
     */
    record Word(int start, String str) implements Token {}
    
    /**
     * Represents a literal number written. For example {@code 2} or {@code 1.5e10i32}.
     *
     * @param start Beginning index in the class definition
     * @param str   Number itself
     */
    record NumberLiteral(int start, String str) implements Token {}
    
    /**
     * Represents a literal string. For example {@code "abc"}.
     *
     * @param start Beginning index in the class definition
     * @param str   String itself
     */
    record StringLiteral(int start, String str) implements Token {}
    
    /**
     * Represents a whitespace.
     *
     * @param start Beginning index in the class definition
     * @param str   Whitespace itself
     */
    record Whitespace(int start, String str) implements Token {}
    
    /**
     * Represents the comment itself.
     *
     * @param start Beginning index in the class definition
     * @param str   Comment itself
     */
    record Comment(int start, String str) implements Token {}
    
    /**
     * Represents a reserved keyword.
     *
     * @param start Beginning index in the class definition
     * @param kind  Keyword itself
     * @see KeywordKind
     */
    record Keyword(int start, KeywordKind kind) implements Token {}
    
    /**
     * Represents a special character.
     *
     * @param start Beginning index in the class definition
     * @param kind  Symbol itself
     * @see SymbolKind
     */
    record Symbol(int start, SymbolKind kind) implements Token {
        char symbol() {return kind.ch;}
    }
    
    /**
     * Contains all permitted symbols
     */
    enum SymbolKind {
        /** Ampersand symbol: {@code &} */
        AMPERSAND('&'),
        /** Asterisk symbol: {@code *} */
        ASTERISK('*'),
        /** At symbol: {@code @} */
        AT('@'),
        /** Backslash symbol: {@code \} */
        BACK_SLASH('\\'),
        /** Closing bracket symbol: {@code )} */
        BRACKET_CLOSE(')'),
        /** Opening bracket symbol: {@code (} */
        BRACKET_OPEN('('),
        /** Coma symbol: {@code ,} */
        COMA(','),
        /** Colon symbol: {@code :} */
        COLON(':'),
        /** Closing curly bracket symbol: <code>}</code> */
        CURLY_BRACKET_CLOSE('}'),
        /** Opening curly bracket symbol: <code>{</code> */
        CURLY_BRACKET_OPEN('{'),
        /** Double quotation marks symbol: {@code "} */
        DOUBLE_QUOTATION_MARK('"'),
        /** Equals symbol: {@code =} */
        EQUALS('='),
        /** Exclamation mark symbol: {@code !} */
        EXCLAMATION_MARK('!'),
        /** Forward slash symbol: {@code /} */
        FORWARD_SLASH('/'),
        /** Less than symbol: {@code <} */
        LESS_THAN('<'),
        /** Minus symbol: {@code -} */
        MINUS('-'),
        /** More than symbol: {@code >} */
        MORE_THAN('>'),
        /** Period symbol: {@code .} */
        PERIOD('.'),
        /** Pipe symbol: {@code |} */
        PIPE('|'),
        /** Plus symbol: {@code +} */
        PLUS('+'),
        /** Question mark symbol: {@code ?} */
        QUESTION_MARK('?'),
        /** Semicolon symbol: {@code ;} */
        SEMICOLON(';'),
        /** Simple quotation mark symbol: {@code '} */
        SIMPLE_QUOTATION_MARK('\''),
        /** Closing square bracket symbol: {@code ]} */
        SQUARE_BRACKET_CLOSE(']'),
        /** Opening square bracket symbol: {@code [} */
        SQUARE_BRACKET_OPEN('[')
        ;
        final char ch;
        SymbolKind(char ch) { this.ch = ch; }
    }
    
    sealed interface KeywordKind permits DataTypeKind, FlowTypeKind, ModifierTypeKind, TypeTypeKind {
        String text();
    }
    
    enum DataTypeKind implements KeywordKind {
        VOID("void"),
        NULL("null"),
        INT32("int32"),
        INT64("int64"),
        FLOAT32("float32"),
        FLOAT64("float64")
        ;
        final String text;
        DataTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    enum FlowTypeKind implements KeywordKind {
        ;
        final String text;
        FlowTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    enum ModifierTypeKind implements KeywordKind {
        ;
        final String text;
        ModifierTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    enum TypeTypeKind implements KeywordKind {
        ;
        final String text;
        TypeTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    final class Mapper {
        private Mapper() {
            throw new UnsupportedOperationException("Don't instantiate Mapper");
        }
        
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
        
        /**
         * Returns a keyword token if the given word is a reserved keyword.
         *
         * @param word to be checked
         * @return a keyword token or the word given.
         * @see Token.Word
         * @see Token.KeywordKind
         */
        public static Token classifyWord(Word word) {
            for (KeywordKind kk : KEYWORD_KIND_LIST) {
                if (kk.text().equals(word.str)) {
                    return new Token.Keyword(word.start, kk);
                }
            }
            return word;
        }
    }
    
    static boolean isWhitespace(Token token) {
        return token instanceof Token.Whitespace;
    }
}
