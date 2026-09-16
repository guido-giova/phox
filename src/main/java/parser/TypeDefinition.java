package parser;

import java.util.List;

public record TypeDefinition(String typeName, List<Token> tokens) {}
