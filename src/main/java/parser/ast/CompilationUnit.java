package parser.ast;

import java.util.List;

/**
 * Unit of data for a file's information.
 *
 * @param packageNode Node of the package
 * @param importNodes List of import nodes
 * @param typeNodes   List of type nodes
 */
public record CompilationUnit(
        PackageDeclarationNode packageNode,
        List<ImportDeclarationNode> importNodes,
        List<TypeNode> typeNodes
) {}
