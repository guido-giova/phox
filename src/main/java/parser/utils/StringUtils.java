package parser.utils;

/**
 * Utility class for working with Strings
 *
 * @see String
 */
public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("Don't instantiate StringUtils");
    }
    
    /**
     * Tests if the given string ends with the specified suffix ignoring the case of both.
     *
     * @param base   String that will be tested on.
     * @param suffix Desired suffix.
     * @return {@code true} if base ends in suffix ignoring the case, {@code false} otherwise.
     */
    public static boolean endsWithIgnoreCase(String base, String suffix) {
        return base.toLowerCase().endsWith(suffix.toLowerCase());
    }
}
