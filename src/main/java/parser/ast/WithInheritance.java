package parser.ast;

/**
 * Trait for AST nodes that specify an explicit inheritance level.
 */
public interface WithInheritance {
    /**
     * Returns the inheritance modifier associated with this AST element.
     *
     * @return the inheritance type kind
     */
    parser.Token.InheritanceTypeKind inheritance();
}
