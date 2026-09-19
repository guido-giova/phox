package parser;

import java.util.List;

public record TokenizedDefinition(
        SourceFile source,
        List<Token> tokens
) {}
