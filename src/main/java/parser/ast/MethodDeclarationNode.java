package parser.ast;

import parser.Token;

import java.util.List;

public record MethodDeclarationNode(
        Token.VisibilityTypeKind visibility,
        Token.DynamismTypeKind dynamism,
        Token.InheritanceTypeKind inheritance,
        TypeReference type,
        String name,
        List<Parameter> params,
        MethodBody body
) implements ASTNode {}
