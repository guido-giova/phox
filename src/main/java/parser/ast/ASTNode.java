package parser.ast;

/**
 * Each node of the Abstract Syntax Tree
 */
public sealed interface ASTNode
                permits PackageDeclarationNode,
                        ImportDeclarationNode,
                        TypeNode,
                        FieldDeclarationNode,
                        MethodDeclarationNode {}
