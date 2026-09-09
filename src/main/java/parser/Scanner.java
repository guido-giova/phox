package parser;

import java.util.List;

public class Scanner {
    public static List<String> scan(String text) {
        final List<String> tokens = new java.util.ArrayList<>();
        final int ll = text.length();
        int ii = 0; // index
        int plainStart = 0;
        
        while (ii < ll) {
            char cc = text.charAt(ii);
            
            if (cc == '\"' || cc == '\'') {
                
            }
            
            ii++;
        }
        
        return tokens;
    }
}
