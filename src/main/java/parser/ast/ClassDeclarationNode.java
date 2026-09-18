package parser.ast;

import parser.Token;

import java.util.List;

/**
 * Declaration of a class given a name, generics, fields, methods, etc.
 *
 * @param visibility  of the class
 * @param inheritance of the class
 * @param dynamism    of the class
 * @param name        of the class
 * @param generics    that it has (empty for none)
 * @param fields      that the class has
 * @param methods     that the class has
 * @param childTypes  that the class has
 */
public record ClassDeclarationNode(
        Token.VisibilityTypeKind visibility,
        Token.InheritanceTypeKind inheritance,
        Token.DynamismTypeKind dynamism,
        String name,
        List<String> generics,
        List<FieldDeclarationNode> fields,
        List<MethodDeclarationNode> methods,
        List<TypeNode> childTypes
) implements TypeNode,
             WithVisibility,
             WithInheritance,
             WithDynamism,
             WithName {}
