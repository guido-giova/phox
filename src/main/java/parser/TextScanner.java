package parser;

public abstract class TextScanner {
    protected final String text;
    protected final int length;
    protected final int beginIndex;
    protected int index;
    
    public TextScanner(String text, int beginIndex) {
        this.text       = text;
        this.length     = text.length();
        this.beginIndex = beginIndex;
        this.index      = beginIndex;
    }
    
    /**
     * @return {@code true} if this has a current value, {@code false} otherwise.
     */
    protected boolean hasCurrent() {
        return this.index < this.length;
    }
    
    /**
     * @return the current character.
     */
    protected char getCurrent() {
        return this.text.charAt(this.index);
    }
    
    /**
     * @return the current character as a String.
     */
    protected String getCurrentAsString() {
        return Character.toString(this.getCurrent());
    }
    
    /**
     * Skips forward by the given amount and returns the old index.
     *
     * @param n Amount of indices skipped.
     * @return  The old index.
     */
    protected int consume(int n) {
        int lastIndex = this.index;
        this.index += n;
        return lastIndex;
    }
    
    /**
     * Skips forward by the one and returns the old index.
     *
     * @return The old index.
     */
    protected int consume() {
        return this.consume(1);
    }
}
