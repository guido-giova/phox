package parser;

import parser.ast.ASTNode;

import java.util.List;

/**
 * Utility class that accepts a list of tokens and outputs an Abstract Syntax Tree of the file.
 */
public final class Syntaxer {
    private Syntaxer() {
        throw new UnsupportedOperationException("Don't instantiate Syntaxer");
    }
    
    /**
     * Given a list of tokens, interpretes the syntax and creates an Abstract Syntax Tree
     *
     * @param tokens that will be resolved
     * @return the parent node
     */
    public static ASTNode resolve(List<Token> tokens) {
        
        return null;
    }
}
