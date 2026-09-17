package parser;

import java.util.List;

public sealed interface ASTNode {
    record FieldDeclarationNode(
        String name,
        TypeReference type,
        List<Token.ModifierTypeKind> modifiers,
        Token definition
    ) implements ASTNode {}
}
