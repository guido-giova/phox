package parser.ast;

import parser.Token;

/**
 * Declaration of a field given a name, type, definition.
 *
 * @param name        of the field
 * @param type        that it has (empty for none)
 * @param visibility  of the field
 * @param dynamism    of the field
 * @param inheritance of the field
 * @param definition  that the field has
 */
public record FieldDeclarationNode(
        String name,
        TypeReference type,
        Token.VisibilityTypeKind visibility,
        Token.InheritanceTypeKind inheritance,
        Token.DynamismTypeKind dynamism,
        Token definition
) implements ASTNode,
             WithName,
             WithVisibility,
             WithDynamism,
             WithInheritance {}
