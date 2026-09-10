package parser;

import java.util.List;

/**
 * Allows the transformation from the Word tokens to Keyword tokens if they are such
 */
public final class Identifier {
    private Identifier() {
        throw new UnsupportedOperationException("Don't instantiate Identifier");
    }
    
    /**
     * From the given list of tokens, the Word tokens get transformed into Keyword tokens if possible.
     *
     * @param tokens that will be mapped
     * @return a new List with the tokens updated
     */
    public static List<Token> identify(List<Token> tokens) {
        return tokens.stream()
                     .map(Identifier::findKeyword)
                     .toList();
    }
    
    /**
     * Filters through tokens to find words. Words are then mapped by Token.Mapper
     *
     * @param token that will be mapped
     * @return the transformed token if valid, the same if not
     * @see Token.Mapper
     */
    private static Token findKeyword(Token token) {
        if (token instanceof Token.Word w) {
            return Token.Mapper.classifyWord(w);
        }
        return token;
    }
}
