package parser.ast;

import parser.Token;

import java.util.List;

public sealed interface ASTNode {
    record FieldDeclarationNode(
        String name,
        TypeReference type,
        Token.VisibilityTypeKind visibility,
        Token.InheritanceTypeKind inheritance,
        Token.DynamismTypeKind dynamism,
        Token definition
    ) implements ASTNode {}
    
    record MethodDeclarationNode(
            Token.VisibilityTypeKind visibility,
            Token.DynamismTypeKind dynamism,
            Token.InheritanceTypeKind inheritance,
            TypeReference type,
            String name,
            List<Parameter> params,
            MethodBody body
    ) implements ASTNode {}
    
    sealed interface MethodBody {
        record Unparsed(List<Token> tokens) implements MethodBody {}
        record Parsed(String block) implements MethodBody {} // todo: create statement block
    }
}
