package parser.ast;

/**
 * Trait for AST nodes that possess a unique string identifier or name
 * (e.g., class names, method names, variable identifiers).
 */
public interface WithName {
    /**
     * Returns the name or identifier of this AST element.
     *
     * @return the name of the node
     */
    String name();
}
