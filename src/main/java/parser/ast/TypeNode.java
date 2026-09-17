package parser.ast;

/**
 * Encompasses the different types: classes, structures, and enums
 */
public sealed interface TypeNode
                extends ASTNode
                permits ClassDeclarationNode {}
