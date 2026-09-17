package parser.ast;

import parser.Token;

public record FieldDeclarationNode(
        String name,
        TypeReference type,
        Token.VisibilityTypeKind visibility,
        Token.InheritanceTypeKind inheritance,
        Token.DynamismTypeKind dynamism,
        Token definition
) implements ASTNode {}
