package parser.ast;

/**
 * Trait for AST nodes that specify an explicit visibility level.
 */
public interface WithVisibility {
    /**
     * Returns the visibility modifier associated with this AST element.
     *
     * @return the visibility type kind
     */
    parser.Token.VisibilityTypeKind visibility();
}
