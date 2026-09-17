package parser.ast;

import parser.Token;

import java.util.List;

record ClassDeclarationNode(
        String name,
        List<String> generics,
        Token.VisibilityTypeKind visibility,
        Token.DynamismTypeKind dynamism,
        Token.InheritanceTypeKind inheritance,
        List<FieldDeclarationNode> fields,
        List<MethodDeclarationNode> methods,
        List<TypeNode> childTypes
) implements TypeNode,
             WithName,
             WithVisibility,
             WithDynamism,
             WithInheritance {}
