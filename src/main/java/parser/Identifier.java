package parser;

import java.util.List;

public class Identifier {
    public static List<Token> identify(List<Token> tokens) {
        return tokens.stream()
                     .map(Identifier::findKeyword)
                     .toList();
    }
    
    private static Token findKeyword(Token token) {
        if (token instanceof Token.Word w) {
            return Token.Mapper.classifyWord(w);
        }
        return token;
    }
}
