package parser.utils;

public class StringUtils {
    public static boolean endsWithIgnoreCase(String base, String suffix) {
        return base.toLowerCase().endsWith(suffix.toLowerCase());
    }
}
