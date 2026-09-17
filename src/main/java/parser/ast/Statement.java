package parser.ast;

import java.util.List;

public sealed interface Statement {
    record Block(List<Statement> statements) implements Statement {}
}
