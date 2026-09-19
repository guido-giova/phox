package parser;

import java.util.List;

public final class Coordinator {
    private Coordinator() {
        throw new UnsupportedOperationException("Don't instantiate Coordinator");
    }
    
    public static void coordinate(List<FileDefinition> definitions) {
        definitions.stream()
                   .map(Coordinator::tokenize);
    }
    
    private static TokenizedDefinition tokenize(FileDefinition data) {
        List<Token> tokens;
        tokens = Scanner.scan(data.fileData());
        tokens = Identifier.identify(tokens);
        tokens = Cleaner.clean(tokens);
        return new TokenizedDefinition(data.source(), tokens);
    }
}
