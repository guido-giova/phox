package parser;

import java.util.List;

public class Cleaner {
    public static List<Token> clean(List<Token> tokens) {
        return tokens.stream()
                     .filter(java.util.function.Predicate.not(t -> t instanceof Token.Whitespace))
                     .toList();
    }
}
