package parser.ast;

import parser.Token;

/**
 * Declaration of a field given a name, type, definition.
 *
 * @param visibility  of the field
 * @param inheritance of the field
 * @param dynamism    of the field
 * @param name        of the field
 * @param type        that it has (empty for none)
 * @param definition  that the field has
 */
public record FieldDeclarationNode(
        Token.VisibilityTypeKind visibility,
        Token.InheritanceTypeKind inheritance,
        Token.DynamismTypeKind dynamism,
        String name,
        TypeReference type,
        Token definition
) implements ASTNode,
             WithVisibility,
             WithInheritance,
             WithDynamism,
             WithName {}
