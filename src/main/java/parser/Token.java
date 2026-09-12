package parser;

import java.util.List;
import java.util.Optional;

/**
 * Represents each element of a class: comments, words, string literals, number literals, symbols, etc.
 */
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
        /** Caret symbol: {@code ^}*/
        CARET('^'),
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
    
    /**
     * Represents the different types of keywords that exist.
     */
    sealed interface KeywordKind permits DataTypeKind, FlowTypeKind, ModifierTypeKind, TypeTypeKind {
        /**
         * @return the string it reserves
         */
        String text();
    }
    
    /**
     * Represents the primitives and other data types.
     */
    enum DataTypeKind implements KeywordKind {
        /** void primitive */
        VOID("void"),
        /** null primitive */
        NULL("null"),
        /** int32 primitive */
        INT32("int32"),
        /** int64 primitive */
        INT64("int64"),
        /** float32 primitive */
        FLOAT32("float32"),
        /** float64 primitive */
        FLOAT64("float64")
        ;
        final String text;
        DataTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    /**
     * Represents the keywords that define code flow
     */
    enum FlowTypeKind implements KeywordKind {
        ;
        final String text;
        FlowTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    /**
     * Represents the keywords that define the modifiers of methods, classes, structures, variables, etc.
     */
    enum ModifierTypeKind implements KeywordKind {
        ;
        final String text;
        ModifierTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    /**
     * Represents the keywords for class and structure definition
     */
    enum TypeTypeKind implements KeywordKind {
        ;
        final String text;
        TypeTypeKind(String text) {this.text = text;}
        @Override public String text() {return this.text;}
    }
    
    /**
     * Utility class for token transformations
     */
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
        
        /**
         * Get the symbol token of the given char.
         *
         * @param chr   of the symbol
         * @param start of the symbol
         * @return A Symbol token that represents the given char
         */
        public static Optional<Token> getToken(char chr, int start) {
            return Optional.ofNullable(BY_CHAR.get(chr))
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
    
    /**
     * Determines if a token is a Whitespace token
     *
     * @param token that will be checked
     * @return {@code true} if it's a whitespace token, {@code false} otherwise
     */
    static boolean isWhitespace(Token token) {
        return token instanceof Token.Whitespace;
    }
}
