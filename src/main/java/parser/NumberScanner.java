package parser;

import utils.CharPredicate;
import utils.Chars;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class NumberScanner {
    private enum Base {
        HEXADECIMAL(16, 'x'),
        DECIMAL(10, 'd'),
        OCTAL(8, 'o'),
        BINARY(2, 'b');
        
        final int base;
        final char symbol;
        Base(int base, char symbol) {
            this.base   = base;
            this.symbol = symbol;
        }
    }
    
    private enum Exponent {
        BINARY(2, 'p'),
        SCIENTIFIC(10, 's');
        
        final int base;
        final char symbol;
        Exponent(int base, char symbol) {
            this.base   = base;
            this.symbol = symbol;
        }
    }
    
    record NumberScannerResponse(Token token, int endIndex) {}
    
    private static final Map<String, Token.DataTypeKind> PREFIXES = Map.of(
            "i32", Token.DataTypeKind.INT32,
            "i64", Token.DataTypeKind.INT64,
            "f32", Token.DataTypeKind.FLOAT32,
            "f64", Token.DataTypeKind.FLOAT64
    );
    
    private final String text;
    private final int length;
    private final int beginIndex;
    private int index;
    private Token.DataTypeKind kind;
    private Base base;
    private String wholePart;
    private String decimalPart;
    private Exponent exponentType;
    private String exponentPart;
    
    private NumberScanner(String text, int beginIndex) {
        this.text       = text;
        this.length     = text.length();
        this.beginIndex = beginIndex;
        this.index      = beginIndex;
        
        this.kind = null;
        this.base = null;
        this.wholePart = null;
        this.decimalPart = null;
        this.exponentType = null;
        this.exponentPart = null;
    }
    
    static NumberScannerResponse scanNumber(String text, int beginIndex) {
        return new NumberScanner(text, beginIndex).scanNumber();
    }
    
    private NumberScannerResponse scanNumber() {
        if (getCurrent() != '0') {
            this.scanDecimalNumber();
            return new NumberScannerResponse(this.createNumberliteralToken(), this.index);
        }
        
        
        
        return null;
    }
    
    private Token createNumberliteralToken() {
        System.out.println(this);
        return new Token.NumberLiteral.Float64(this.beginIndex, "0.0d", 0.0d);
    }
    
    private void scanDecimalNumber() {
        this.base = Base.DECIMAL;
        this.kind = Token.DataTypeKind.INT32;
        
        this.wholePart = this.consumeDigitRun(Chars::isDecDigit);
        
        boolean hasPeriod = this.isCurrentPeriod();
        if (hasPeriod) {
            this.index++;
            this.kind = Token.DataTypeKind.FLOAT64;
            this.decimalPart = this.consumeDigitRun(Chars::isDecDigit);
        }
        
        Optional<Exponent> hasExponent = this.isCurrentExponent();
        if (hasExponent.isPresent()) {
            this.index++;
            this.exponentType = hasExponent.get();
            String exponentPart = "";
            if (Chars.isSignSymbol(this.getCurrent())) {
                exponentPart = Character.toString(this.getCurrent());
                this.index++;
            }
            this.exponentPart = exponentPart + this.consumeDigitRun(Chars::isDecDigit);
        }
    }
    
    private char getCurrent() {
        return this.text.charAt(this.index);
    }
    
    private boolean isCurrentPeriod() {
        return this.getCurrent() == '.';
    }
    
    private Optional<Exponent> isCurrentExponent() {
        return Arrays.stream(Exponent.values())
                     .filter(e -> e.symbol == this.getCurrent())
                     .findFirst();
    }
    
    private boolean hasNext() {
        return this.index <= this.length;
    }
    
    private boolean hasNNext(int count) {
        return this.index + count < this.length;
    }
    
    private String consumeDigitRun(CharPredicate isDigit) {
        if (! this.hasNext() || isDigit.negate().test(getCurrent())) {
            throw new IllegalArgumentException("Expected digit at " + this.index);
        }
        StringBuilder sb = new StringBuilder();
        while (this.hasNext()) {
            char cc = this.getCurrent();
            if (isDigit.test(cc)) {
                sb.append(cc);
                this.index++;
            } else if (cc == '_' && this.hasNNext(1) && isDigit.test(this.text.charAt(this.index + 1))) {
                this.index++; // skip separator; loop consumes the digit right after it
            } else {
                return sb.toString();
            }
        }
        return "It stopped lol";
    }
    
    @Override
    public String toString() {
        return "NumberScanner{" +
               "\n    kind=" + kind +
               "\n    base=" + base +
               "\n    wholePart='" + wholePart + '\'' +
               "\n    decimalPart='" + decimalPart + '\'' +
               "\n    exponentType=" + exponentType +
               "\n    exponentPart='" + exponentPart + '\'' +
               "\n}";
    }
}
