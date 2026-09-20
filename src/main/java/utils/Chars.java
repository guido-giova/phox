package utils;

import java.util.concurrent.atomic.AtomicInteger;

public class Chars {
    public static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
    
    public static boolean isBinDigit(char c) {
        return c == '0' || c == '1';
    }
    
    public static boolean isOctDigit(char c) {
        return c >= '0' && c <= '7';
    }
    
    public static boolean isDecDigit(char c) {
        return c >= '0' && c <= '9' || c == '_';
    }
    
    public static boolean isHexDigit(char c) {
        return Chars.isDecDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    
    public static boolean isExponentSymbol(char c) {
        return c == 'e' || c == 'E';
    }
    
    public static boolean isSignSymbol(char c) {
        return c == '+' || c == '-';
    }
    
    public static char charAt(String text, AtomicInteger ii) {
        return text.charAt(ii.get());
    }
    
    public static boolean matchesIgnoreCase(String text, AtomicInteger ii, String prefix) {
        return text.regionMatches(ii.get(), prefix,               0, prefix.length())
            || text.regionMatches(ii.get(), prefix.toUpperCase(), 0, prefix.length());
    }
}
