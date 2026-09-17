package parser.ast;

import parser.Token;

import java.util.List;

sealed interface TypeReference {
    record Primitive(Token.DataTypeKind kind) implements TypeReference {}
    record Named(Token.Word name, List<TypeReference> generics) implements TypeReference {}
    record Pointer(TypeReference baseType) implements TypeReference {}
}
