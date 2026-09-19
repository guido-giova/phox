package parser.ast;

import parser.Token;

import java.util.List;

/**
 * Definition of the body of a method. Methods can be parsed and unparsed, depending on the parsing phase.
 */
public sealed interface MethodBody {
    /**
     * Unparsed tokens inside the method
     *
     * @param tokens of the method
     */
    record Unparsed(List<Token> tokens) implements MethodBody {}
    
    /**
     * Block of statements that the method contains
     *
     * @param block of statements
     */
    record Parsed(Statement.Block block) implements MethodBody {}
}
