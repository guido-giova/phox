package parser;

import parser.ast.ASTNode;

public record TreeDefinition(
        SourceFile source,
        ASTNode node
) {}
