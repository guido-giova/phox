package parser.ast;

/**
 * Definition of a parameter for a method
 *
 * @param name of the parameter
 * @param type of the parameter
 */
public record Parameter(String name, TypeReference type) {}
