package parser;

import java.util.List;

/**
 * TokenStream is used for reading through a list of tokens to expect types and move along the list.
 */
public class TokenStream {
    private final List<Token> tokens;
    
    private int position;
    
    /**
     * Constructor for the TokenStream
     *
     * @param tokens list of tokens
     */
    public TokenStream(List<Token> tokens) {
        this.tokens = List.copyOf(tokens);
        this.position = 0;
    }
    
    /**
     * @return the token at the current position
     */
    public Token peek() {
        if (this.position >= this.tokens.size()) {
            return null;
        }
        return this.tokens.get(this.position);
    }
    
    /**
     * Returns the element at the current position and increases the current position by 1.
     *
     * @return the element at the current position
     */
    public Token consume() {
        Token current = this.peek();
        this.position++;
        return current;
    }
    
    /**
     * Check if the current token matches the expected symbol kind and consume it
     *
     * @param kind that is expected
     * @return {@code true} if current token matches expectation, {@code false} otherwise
     */
    public boolean matchSymbol(Token.SymbolKind kind) {
        if (this.peek() instanceof Token.Symbol s && s.kind() == kind) {
            this.consume();
            return true;
        }
        return false;
    }
    
    /**
     * Expect a specific token. Throws an exception if absent
     *
     * @param kind that is expected
     * @return the specific expected token, exception otherwise
     */
    public Token expectSymbol(Token.SymbolKind kind) {
        if (this.matchSymbol(kind)) {
            return this.tokens.get(this.position - 1);
        }
        Token actual = this.peek();
        throw new IllegalArgumentException("Expected " + kind + " at position " + (actual != null ? actual.start() : "EOF"));
    }
    
    /**
     * Expect a semicolon ';'.
     *
     * @return the current semicolon token
     */
    public Token expectSemicolon() {
        return this.expectSymbol(Token.SymbolKind.SEMICOLON);
    }
    
    /**
     * @return {@code true} if there are more tokens, {@code false} otherwise
     */
    public boolean hasNext() {
        return this.position < this.tokens.size();
    }
}
