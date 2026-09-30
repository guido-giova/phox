package parser.ast;

import parser.Token;

public sealed interface Expression {
    record Variable(String name) implements Expression {}
    record Number(Token.NumberLiteral number) implements Expression {}
    record Call(String name, Expression... parameters) implements Expression {}
}
