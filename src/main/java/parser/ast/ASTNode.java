package parser.ast;

public sealed interface ASTNode
                permits FieldDeclarationNode,
                        MethodDeclarationNode,
                        TypeNode {
}
