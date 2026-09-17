package parser.ast;

/**
 * Trait for AST nodes that specify an explicit dynamism.
 */
public interface WithDynamism {
    /**
     * Returns the dynamism modifier associated with this AST element.
     *
     * @return the dynamism type kind
     */
    parser.Token.DynamismTypeKind dynamism();
}
