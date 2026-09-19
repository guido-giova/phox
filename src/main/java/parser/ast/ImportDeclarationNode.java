package parser.ast;

public record ImportDeclarationNode(
        String importName
) implements ASTNode {}
