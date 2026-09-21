package parser;

import utils.CharPredicate;
import utils.Chars;

import java.math.BigDecimal;
import java.math.MathContext;
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
        
        public static Optional<Base> getBySymbol(char symbol) {
            return Arrays.stream(Base.values())
                         .filter(b -> b.symbol == symbol)
                         .findFirst();
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
        
        public static Optional<Exponent> getBySymbol(char symbol) {
            return Arrays.stream(Exponent.values())
                         .filter(e -> e.symbol == symbol)
                         .findFirst();
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
    
    private BigDecimal value;
    private Token.DataTypeKind kind;
    private Token.DataTypeKind desiredKind;
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
        
        this.value = null;
        this.kind = null;
        this.desiredKind = null;
        this.base = null;
        this.wholePart = null;
        this.decimalPart = null;
        this.exponentType = null;
        this.exponentPart = null;
    }
    
    private char getCurrent() {return this.text.charAt(this.index);}
    
    private boolean isCurrentPeriod() {return this.getCurrent() == '.';}
    
    private Optional<Exponent> isCurrentExponent() {
        return Exponent.getBySymbol(this.getCurrent());
    }
    
    private boolean hasCurrent() {return this.index < this.length;}
    
    private boolean hasAhead(int count) {return this.index + count < this.length;}
    
    private int consume(int n) {
        int lastIndex = this.index;
        this.index += n;
        return lastIndex;
    }
    
    private int consume() {
        return this.consume(1);
    }
    
    static NumberScannerResponse scanNumber(String text, int beginIndex) {
        return new NumberScanner(text, beginIndex).scanNumber();
    }
    
    private NumberScannerResponse scanNumber() {
        if (this.getCurrent() != '0') {
            this.scanDecimalNumber();
            return new NumberScannerResponse(this.createNumberliteralToken(), this.index);
        }
        this.consume();
        this.checkForType();
        this.checkForBase();
        if (this.base == null) {
            this.base = Base.DECIMAL;
            if (this.desiredKind == null) {
                this.index--;
            }
            this.scanDecimalNumber();
            return new NumberScannerResponse(this.createNumberliteralToken(), this.index);
        }
        
        
        return null;
    }
    
    private Token createNumberliteralToken() {
        final String numberString = this.text.substring(this.beginIndex, this.index);
        return switch (this.kind) {
            case INT32   -> new Token.NumberLiteral.Int32(this.beginIndex, numberString, this.value.intValue());
            case INT64   -> new Token.NumberLiteral.Int64(this.beginIndex, numberString, this.value.longValue());
            case FLOAT32 -> new Token.NumberLiteral.Float32(this.beginIndex, numberString, this.value.floatValue());
            case FLOAT64 -> new Token.NumberLiteral.Float64(this.beginIndex, numberString, this.value.doubleValue());
            default      -> throw new IllegalStateException("Unexpected value: " + this.kind);
        };
    }
    
    private void scanDecimalNumber() {
        this.wholePart = this.consumeDigitRun(Chars::isDecDigit);
        
        if (this.isCurrentPeriod()) {
            this.consume();
            this.decimalPart = this.consumeDigitRun(Chars::isDecDigit);
        }
        
        Optional<Exponent> hasExponent = this.isCurrentExponent();
        if (hasExponent.isPresent()) {
            this.consume();
            this.exponentType = hasExponent.get();
            
            String sign = "";
            if (Chars.isSignSymbol(this.getCurrent())) {
                sign = Character.toString(this.getCurrent());
                this.consume();
            }
            this.exponentPart = sign + this.consumeDigitRun(Chars::isDecDigit);
        }
        
        this.base = Base.DECIMAL;
        this.computeValue();
        this.resolveKind();
    }
    
    private void computeValue() {
        String digits = this.wholePart;
        if (this.decimalPart != null) {
            digits += '.' + this.decimalPart;
        }
        BigDecimal tempValue = new BigDecimal(digits);
        
        if (this.exponentPart == null) {
            this.value = tempValue;
            return;
        }
        int exp = Integer.parseInt(this.exponentPart);
        BigDecimal expBase = BigDecimal.valueOf(this.exponentType.base);
        
        if (exp >= 0) {
            this.value = tempValue.multiply(expBase.pow(exp));
        } else {
            this.value = tempValue.divide(expBase.pow(-exp), MathContext.UNLIMITED);
        }
    }
    
    private void resolveKind() {
        boolean isIntegral = this.value.stripTrailingZeros().scale() <= 0;
        
        if (this.desiredKind != null) {
            if (! isIntegral && ! Token.DataTypeKind.isFloatingPoint(this.desiredKind)) {
                throw new IllegalArgumentException("Value " + value + " has a fractional result but " + this.desiredKind + " can't hold decimals at " + this.index);
            }
            this.kind = this.desiredKind;
            return;
        }
        
        this.kind = isIntegral ? Token.DataTypeKind.INT32 : Token.DataTypeKind.FLOAT64;
    }
    
    private String consumeDigitRun(CharPredicate isDigit) {
        if (! this.hasCurrent() || isDigit.negate().test(getCurrent())) {
            throw new IllegalArgumentException("Expected digit at " + this.index);
        }
        StringBuilder sb = new StringBuilder();
        while (this.hasCurrent()) {
            char cc = this.getCurrent();
            if (isDigit.test(cc)) {
                sb.append(cc);
                this.consume();
            } else if (cc == '_' && this.hasAhead(1) && isDigit.test(this.text.charAt(this.index + 1))) {
                this.consume(); // skip separator; loop consumes the digit right after it
            } else {
                return sb.toString();
            }
        }
        return "It stopped lol";
    }
    
    private void checkForType() {
        if(! this.hasAhead(3)) {return;}
        Token.DataTypeKind chosenKind = PREFIXES.get(this.text.substring(this.index, this.index + 3));
        if (chosenKind != null) {
            this.desiredKind = chosenKind;
            this.consume(3);
        }
    }
    
    private void checkForBase() {
        if (! this.hasCurrent()) {return;}
        Optional<Base> chosenBase = Base.getBySymbol(this.getCurrent());
        if (chosenBase.isPresent()) {
            this.base = chosenBase.get();
            this.consume();
        }
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
