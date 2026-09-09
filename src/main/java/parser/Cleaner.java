package parser;

import java.util.List;
import java.util.function.Predicate;

public class Cleaner {
    public static List<Token> clean(List<Token> tokens) {
        return tokens.stream()
                     .filter(Predicate.not(Token::isWhitespace))
                     .toList();
    }
}
