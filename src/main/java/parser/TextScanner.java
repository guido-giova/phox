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
    
}
