package parser;

import java.util.List;
import java.util.function.Predicate;

/**
 * Cleans the list of tokens from all the unneeded whitespace tokens
 */
public final class Cleaner {
    private Cleaner() {
        throw new UnsupportedOperationException("Don't instantiate Cleaner");
    }
    
    /**
     * Filters through the given list of tokens to remove the unneeded whitespace tokens.
     *
     * @param tokens that will be filtered through
     * @return a new list with no whitespace tokens
     */
    public static List<Token> clean(List<Token> tokens) {
        return tokens.stream()
                     .filter(Predicate.not(Token::isWhitespace))
                     .toList();
    }
}
