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
        return this.hasRun(1);
    }
    
    /**
     * Checks whether at least {@code count} characters remain, starting at the current index (inclusive).
     *
     * <p>Equivalent to {@code index + count <= length}. For example, {@code hasRun(1)} is true when there is a current
     * character, and {@code hasRun(2)} is true when both the current character and the one after it exist.
     *
     * @param count the number of characters required, starting at the current index
     * @return {@code true} if at least {@code count} characters remain from the current index, {@code false} otherwise
     */
    protected boolean hasRun(int count) {
        return this.index + count <= this.length;
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
    
    /**
     * Returns a substring of text starting at the given index and ending at the end of the string.
     *
     * @param beginIndex The beginning index (inclusive).
     * @return A substring of the current text.
     */
    protected String substring(int beginIndex) {
        return this.substring(beginIndex, this.length);
    }
    
    /**
     * Returns a substring of text starting at beginIndex and ending at endIndex.
     *
     * @param beginIndex The beginning index (inclusive).
     * @param endIndex   The ending index (exclusive).
     * @return A substring of the current text.
     */
    protected String substring(int beginIndex, int endIndex) {
        return this.text.substring(beginIndex, endIndex);
    }
}
