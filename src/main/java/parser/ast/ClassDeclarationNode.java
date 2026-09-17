package parser.ast;

import parser.Token;

import java.util.List;

/**
 * Declaration of a class given a name, generics, fields, methods, etc.
 *
 * @param name        of the class
 * @param generics    that it has (empty for none)
 * @param visibility  of the class
 * @param dynamism    of the class
 * @param inheritance of the class
 * @param fields      that the class has
 * @param methods     that the class has
 * @param childTypes  that the class has
 */
public record ClassDeclarationNode(
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
