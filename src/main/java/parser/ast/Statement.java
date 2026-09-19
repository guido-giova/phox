package parser.ast;

import java.util.List;

/**
 * Defines the different types of statements inside a method
 */
public sealed interface Statement {
    /**
     * Main structure inside a method. Defines a list of statements.
     *
     * @param statements inside a block
     */
    record Block(List<Statement> statements) implements Statement {}
}
