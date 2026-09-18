package parser.ast;

public record PackageDeclarationNode(
        String packageName
) implements ASTNode {}
