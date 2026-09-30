package parser.utils;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("Don't instantiate StringUtils");
    }
    
    public static boolean endsWithIgnoreCase(String base, String suffix) {
        return base.toLowerCase().endsWith(suffix.toLowerCase());
    }
}
