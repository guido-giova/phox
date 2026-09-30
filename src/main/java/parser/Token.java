package parser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Represents each element of a class: comments, words, string literals, number literals, symbols, etc.
 */
public sealed interface Token {
    /**
     * Defines where the token starts
     *
     * @return index where the token begins
     */
    int start();
    
    /**
     * Defines the amount of symbol the token encapsulates.
     * @return the length of the token's value
     */
    int length();
    
    /**
     * Defines the index where the token ends (exclusive).
     *
     * @return the index where the token ends.
     */
    default int end() {
        return this.start() + this.length();
    }
    
    /**
     * Represents any group of alphanumeric symbols that isn't a keyword. This represents class names, variable names,
     * method names, etc.
     *
     * @param start Beginning index in the class definition
     * @param str   Value itself
     */
    record Word(int start, String str) implements Token {
        @Override
        public int length() {return this.str.length();}
    }
    
    /**
     * Represents a literal number written. For example {@code 2} or {@code 0f32x19.fs-2}.
     */
    sealed interface NumberLiteral extends Token {
        /**
         * @return the raw data of the number literal
         */
        String raw();
        
        @Override
        default int length() {
            return this.raw().length();
        }
        
        /**
         * Represents a 32-bit integer.
         *
         * @param start Beginning index in the class definition
         * @param raw   The raw data of the number literal
         * @param value The value itself
         */
        record Int32(int start, String raw, int value) implements NumberLiteral {}
        
        /**
         * Represents a 64-bit integer.
         *
         * @param start Beginning index in the class definition
         * @param raw   The raw data of the number literal
         * @param value The value itself
         */
        record Int64(int start, String raw, long value) implements NumberLiteral {}
        
        /**
         * Represents a 32-bit float.
         *
         * @param start Beginning index in the class definition
         * @param raw   The raw data of the number literal
         * @param value The value itself
         */
        record Float32(int start, String raw, float value) implements NumberLiteral {}
        
        /**
         * Represents a 64-bit float.
         *
         * @param start Beginning index in the class definition
         * @param raw   The raw data of the number literal
         * @param value The value itself
         */
        record Float64(int start, String raw, double value) implements NumberLiteral {}
        
        /**
         * Represents a number literal that is yet to be resolved
         *
         * @param start Beginning index in the class definition
         * @param raw   The raw data of the number literal
         * @param value The value itself
         * @param kind  The kind to be mapped to
         */
        record Unresolved(int start, String raw, BigDecimal value, KeywordKind.Data.Number kind) implements NumberLiteral {}
    }
    
    /**
     * Represents a literal string. For example {@code "abc"}.
     *
     * @param start Beginning index in the class definition
     * @param raw   The raw data of the string literal
     * @param str   String itself
     */
    record StringLiteral(int start, String raw, String str) implements Token {
        @Override
        public int length() {return "\"".length() + this.raw.length() + "\"".length();}
    }
    
    /**
     * Represents a literal character. For example {@code 'a'}.
     *
     * @param start     Beginning index in the class definition
     * @param raw       The raw data of the character literal
     * @param character Character itself
     */
    record CharacterLiteral(int start, String raw, char character) implements Token {
        @Override
        public int length() {return "'".length() + this.raw.length() + "'".length();}
    }
    
    /**
     * Represents a whitespace.
     *
     * @param start Beginning index in the class definition
     * @param str   Whitespace itself
     */
    record Whitespace(int start, String str) implements Token {
        @Override
        public int length() {return this.str.length();}
    }
    
    /**
     * Represents the comment itself.
     *
     * @param start Beginning index in the class definition
     * @param str   Comment itself
     */
    record Comment(int start, String str) implements Token {
        @Override
        public int length() {return "/*".length() + this.str.length() + "*/".length();}
    }
    
    /**
     * Represents a reserved keyword.
     *
     * @param start Beginning index in the class definition
     * @param kind  Keyword itself
     * @see KeywordKind
     */
    record Keyword(int start, KeywordKind kind) implements Token {
        @Override
        public int length() {return this.kind.text().length();}
    }
    
    /**
     * Represents a special character.
     *
     * @param start Beginning index in the class definition
     * @param kind  Symbol itself
     * @see SymbolKind
     */
    record Symbol(int start, SymbolKind kind) implements Token {
        char symbol() {return kind.ch;}
        @Override
        public int length() {return 1;}
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
        /** Caret symbol: {@code ^} */
        CARET('^'),
        /** Comma symbol: {@code ,} */
        COMMA(','),
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
    sealed interface KeywordKind
             permits /* enums */
                     KeywordKind.Flow,
                     /* interfaces */
                     KeywordKind.Data,
                     KeywordKind.Type,
                     KeywordKind.Modifier {
        /**
         * @return the string it reserves
         */
        String text();
        
        /**
         * Returns an Optional with the KeywordKind if found by given text.
         *
         * @param type of the enum string that is being searched
         * @param text that is being searched
         * @param <T>  type that is returned inside the Optional
         * @return An Optional containing the enum's value if found, empty otherwise.
         */
        static <T extends Enum<T> & KeywordKind> Optional<T> getByText(Class<T> type, String text) {
            return Arrays.stream(type.getEnumConstants())
                         .filter(e -> e.text().equals(text))
                         .findFirst();
        }
        
        /**
         * Represents the primitives and other data types.
         */
        sealed interface Data
                 extends KeywordKind
                 permits Data.Number,
                         Data.Other {
            /**
             * Represents data types that are not numbers
             */
            enum Other implements Data {
                /** void primitive */
                VOID("void"),
                /** bool primitive */
                BOOL("bool"),
                ;
                final String text;
                Other(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents data kinds that represents numbers
             */
            enum Number implements Data {
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
                final String prefix;
                Number(String text) {
                    this.text   = text;
                    this.prefix = text.charAt(0) + text.substring(text.length() - 2);
                }
                @Override public String text() {return this.text;}
                public String prefix() {return this.prefix;}
                
                /**
                 * Informs if the given Data represents a floating point number primitive.
                 *
                 * @param kind that wants to be checked
                 * @return {@code true} if it is a floating point number, {@code false} otherwise.
                 */
                public static boolean isFloatingPoint(Data kind) {
                    return kind == FLOAT32 || kind == FLOAT64;
                }
                
                /**
                 * Informs if the given Data represents an integer number primitive.
                 *
                 * @param kind that wants to be checked
                 * @return {@code true} if it is an integer number, {@code false} otherwise.
                 */
                public static boolean isInteger(Data kind) {
                    return kind == INT32 || kind == INT64;
                }
                
                /**
                 * Returns an Optional with the Number if found by given prefix.
                 *
                 * @param prefix that is being searched
                 * @return An Optional containing the Number's value if found, empty otherwise.
                 */
                static Optional<Number> getByPrefix(String prefix) {
                    return Arrays.stream(Number.values())
                                 .filter(e -> e.prefix.equals(prefix))
                                 .findAny();
                }
            }
        }
        
        /**
         * Represents the keywords that define code flow
         */
        enum Flow implements KeywordKind {
            /** Indicates the {@code return} keyword*/
            RETURN("return"),
            /** Indicates the {@code if} keyword*/
            IF("if"),
            /** Indicates the {@code else} keyword*/
            ELSE("else"),
            /** Indicates the {@code for} keyword*/
            FOR("for"),
            /** Indicates the {@code while} keyword*/
            WHILE("while"),
            /** Indicates the {@code switch} keyword*/
            SWITCH("switch")
            ;
            final String text;
            Flow(String text) {this.text = text;}
            @Override public String text() {return this.text;}
        }
        
        /**
         * Represents the keywords for class and structure definition
         */
        sealed interface Type
                 extends KeywordKind
                 permits Type.Creation,
                         Type.Listing,
                         Type.Syntax,
                         Type.Variable {
            /**
             * Represents type creation keywords.
             */
            enum Creation implements Type {
                /** Indicates the {@code class} keyword*/
                CLASS("class"),
                /** Indicates the {@code structure} keyword*/
                STRUCTURE("structure"),
                /** Indicates the {@code enum} keyword*/
                ENUM("enum"),
                /** Indicates the {@code data} keyword*/
                DATA("data"),
                /** Indicates the {@code single} keyword*/
                SINGLE("single");
                final String text;
                Creation(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents modifications of a type.
             */
            enum Listing implements Type {
                /** Indicates the {@code extends} keyword*/
                EXTENDS("extends"),
                /** Indicates the {@code permits} keyword*/
                PERMITS("permits"),
                ;
                final String text;
                Listing(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents the token syntax.
             */
            enum Syntax implements Type {
                /** Indicates the {@code syntax} keyword*/
                SYNTAX("syntax")
                ;
                final String text;
                Syntax(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents the token that refer to the types.
             */
            enum Variable implements Type {
                /** Indicates the {@code this} keyword*/
                THIS("this"),
                /** Indicates the {@code child} keyword*/
                CHILD("child"),
                /** Indicates the {@code super} keyword*/
                SUPER("super")
                ;
                final String text;
                Variable(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
        }
        
        /**
         * Represents the keywords that define the modifiers of methods, classes, structures, variables, etc.
         */
        sealed interface Modifier
                 extends KeywordKind
                 permits Modifier.Visibility,
                         Modifier.Inheritance,
                         Modifier.Dynamism {
            /**
             * Represents the keywords that define visibility
             * <br>
             * There are 4 types of visibility:
             * <table>
             *     <caption>
             *         Types of visibility / access
             *     </caption>
             *     <tr>
             *         <th>Keyword</th>
             *         <th>Visibility rules</th>
             *     </tr>
             *     <tr>
             *         <td>public</td>
             *         <td>Anyone from anywhere can access</td>
             *     </tr>
             *     <tr>
             *         <td>(package-protected)</td>
             *         <td>Only classes inside the same package can access</td>
             *     </tr>
             *     <tr>
             *         <td>protected</td>
             *         <td>Only child classes can access</td>
             *     </tr>
             *     <tr>
             *         <td>private</td>
             *         <td>No class can access (except itself)</td>
             *     </tr>
             * </table>
             */
            enum Visibility implements Modifier {
                /** Indicates the {@code public} keyword*/
                PUBLIC("public"),
                /** Indicates the {@code protected} keyword*/
                PROTECTED("protected"),
                /** Indicates the {@code private} keyword*/
                PRIVATE("private"),
                ;
                final String text;
                Visibility(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents the keywords that define inheritance.
             * <br>
             * There are 4 types of inheritance:
             * <table>
             *     <caption>
             *         Types of inheritance
             *     </caption>
             *     <tr>
             *         <th>Keyword</th>
             *         <th>Inheritance rules</th>
             *     </tr>
             *     <tr>
             *         <td>open</td>
             *         <td>Anyone from anywhere can inherit</td>
             *     </tr>
             *     <tr>
             *         <td>(internal)</td>
             *         <td>Only classes inside the same package can inherit</td>
             *     </tr>
             *     <tr>
             *         <td>sealed</td>
             *         <td>Only the listed classes can inherit</td>
             *     </tr>
             *     <tr>
             *         <td>closed</td>
             *         <td>No class can inherit</td>
             *     </tr>
             * </table>
             */
            enum Inheritance implements Modifier {
                /** Indicates the {@code open} keyword*/
                OPEN("open"),
                /** Indicates the {@code sealed} keyword*/
                SEALED("sealed"),
                /** Indicates the {@code closed} keyword*/
                CLOSED("closed"),
                ;
                final String text;
                Inheritance(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
            
            /**
             * Represents the keywords that define dynamism
             */
            enum Dynamism implements Modifier {
                /** Indicates the {@code static} keyword*/
                STATIC("static"),
                ;
                final String text;
                Dynamism(String text) {this.text = text;}
                @Override public String text() {return this.text;}
            }
        }
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
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Flow.values()));
            
            /* Data kinds */
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Data.Other.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Data.Number.values()));
            
            /* Type kinds */
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Type.Creation.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Type.Listing.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Type.Syntax.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Type.Variable.values()));
            
            /* Modifier kinds */
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Modifier.Visibility.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Modifier.Inheritance.values()));
            KEYWORD_KIND_LIST.addAll(List.of(KeywordKind.Modifier.Dynamism.values()));
            
            BY_CHAR = Arrays.stream(SymbolKind.values())
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
         * Returns a keyword token if the given token is a reserved keyword.
         *
         * @param token to be checked
         * @return a keyword token or the token given.
         * @see Token.Word
         * @see Token.KeywordKind
         */
        public static Token classifyWord(Token token) {
            if (! (token instanceof Word(int start, String str))) {
                return token;
            }
            for (KeywordKind kk : KEYWORD_KIND_LIST) {
                if (kk.text().equals(str)) {
                    return new Token.Keyword(start, kk);
                }
            }
            return token;
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
