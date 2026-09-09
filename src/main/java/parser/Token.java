package parser;

public sealed interface Token {
    int start();
    
    record Word(int start, String str) implements Token {}
    record NumberLiteral(int start, String str) implements Token {}
    record Whitespace(int start, String str) implements Token {}
    record StringLiteral(int start, String str) implements Token {}
    record Comment(int start, String str) implements Token {}
    record Keyword(int start, String str) implements Token {}
    record Symbol(int start, String str) implements Token {}
}
