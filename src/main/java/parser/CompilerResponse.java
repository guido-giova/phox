package parser;

import java.util.List;

public sealed interface CompilerResponse
                permits CompilerResponse.Failed,
                        CompilerResponse.Success {
    record Failed(String reason) implements CompilerResponse {}
    record Success(List<Token> tokens) implements CompilerResponse {}
}
