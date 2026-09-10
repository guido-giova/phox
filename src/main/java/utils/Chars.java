package utils;

import java.util.concurrent.atomic.AtomicInteger;

public class Chars {
    public static boolean isDecDigit(char c) {
        return c >= '0' && c <= '9' || c == '_';
    }
    
    public static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
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
}
