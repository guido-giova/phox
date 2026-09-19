package parser;

import java.util.List;

/**
 * Distributes and organizes the information given to the different stages of compilation
 */
public final class Coordinator {
    private Coordinator() {
        throw new UnsupportedOperationException("Don't instantiate Coordinator");
    }
    
    /**
     * Runs a batch of source files through tokenizing, parsing, and type resolution.
     *
     * @param definitions raw file contents paired with their source metadata
     */
    public static void coordinate(List<FileDefinition> definitions) {
        List<TreeDefinition> trees = definitions.stream()
                                                .map(Coordinator::tokenize)
                                                .map(Coordinator::treeify)
                                                .toList();
        TypeResolver.resolve(trees);
    }
    
    private static TokenizedDefinition tokenize(FileDefinition data) {
        List<Token> tokens;
        tokens = Scanner.scan(data.fileData());
        tokens = Identifier.identify(tokens);
        tokens = Cleaner.clean(tokens);
        return new TokenizedDefinition(data.source(), tokens);
    }
    
    private static TreeDefinition treeify(TokenizedDefinition data) {
        return new TreeDefinition(data.source(), Syntaxer.resolve(data.tokens()));
    }
}
