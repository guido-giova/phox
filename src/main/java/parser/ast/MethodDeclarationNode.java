package parser.ast;

import parser.Token;

import java.util.List;

/**
 * Declaration of a method given the name, type, params, body, etc.
 *
 * @param visibility  of the method
 * @param dynamism    of the method
 * @param inheritance of the method
 * @param type        that is returned
 * @param name        of the method
 * @param params      of the method
 * @param body        of the method (null if empty)
 */
public record MethodDeclarationNode(
        Token.VisibilityTypeKind visibility,
        Token.DynamismTypeKind dynamism,
        Token.InheritanceTypeKind inheritance,
        TypeReference type,
        String name,
        List<Parameter> params,
        MethodBody body
) implements ASTNode,
             WithName,
             WithVisibility,
             WithDynamism,
             WithInheritance {}
