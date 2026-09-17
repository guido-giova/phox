package parser.ast;

import parser.Token;

import java.util.List;

public sealed interface MethodBody {
    record Unparsed(List<Token> tokens) implements MethodBody {}
    record Parsed(String block) implements MethodBody {} // todo: create statement block
}
