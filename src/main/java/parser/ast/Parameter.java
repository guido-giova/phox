package parser.ast;

/**
 * Definition of a parameter for a method
 *
 * @param type of the parameter
 * @param name of the parameter
 */
public record Parameter(TypeReference type, String name) {}
