package utils;

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
        return c >= '0' && c <= '9';
    }
    
    public static boolean isHexDigit(char c) {
        return Chars.isDecDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    
    public static boolean isSignSymbol(char c) {
        return c == '+' || c == '-';
    }
}
