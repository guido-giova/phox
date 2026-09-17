package parser.ast;

import parser.Token;

/**
 * Defines the types for methods, fields, parameters, and statements
 */
public sealed interface TypeReference {
    /**
     * The type is a primitive
     *
     * @param kind of the primitive
     */
    record Primitive(Token.DataTypeKind kind) implements TypeReference {}
    
    /**
     * The type is a class, structure, or enum
     *
     * @param name of the type
     * @param generics that it may have (empty list for none)
     */
    record Named(Token.Word name, java.util.List<TypeReference> generics) implements TypeReference {}
    
    /**
     * The type is a pointer to another reference
     *
     * @param baseType where the pointer points to
     */
    record Pointer(TypeReference baseType) implements TypeReference {}
}
