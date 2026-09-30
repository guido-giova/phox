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
    
    record Definition(TypeNode type, String name) implements Statement {}
    
    record Assignment(String name, Expression expression) implements Statement {}
    
    record Return(Expression expression) implements Statement {}
}
