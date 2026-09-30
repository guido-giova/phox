package parser;

import utils.CharPredicate;
import utils.Chars;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Optional;

public final class NumberScanner extends TextScanner {
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
    
    record NumberScannerResponse(Token.NumberLiteral.Unresolved token, int endIndex) {}
    
    private BigDecimal value;
    private Token.DataTypeKind.Number kind;
    private Token.DataTypeKind.Number desiredKind;
    private Base base;
    private String wholePart;
    private String decimalPart;
    private Exponent exponentType;
    private String exponentPart;
    
    private NumberScanner(String text, int beginIndex) {
        super(text, beginIndex);
        
        this.value        = null;
        this.kind         = null;
        this.desiredKind  = null;
        this.base         = null;
        this.wholePart    = null;
        this.decimalPart  = null;
        this.exponentType = null;
        this.exponentPart = null;
    }
    
    private boolean isCurrentPeriod() {
        if (! this.hasCurrent()) { return false; }
        return this.getCurrent() == '.';
    }
    
    private Optional<Exponent> isCurrentExponent() {
        if (! this.hasCurrent()) { return Optional.empty(); }
        return Exponent.getBySymbol(this.getCurrent());
    }
    
    private boolean hasRun(int count) {return this.index + count <= this.length;}
    
    static NumberScannerResponse scanNumber(String text, int beginIndex) {
        return new NumberScanner(text, beginIndex).scanNumber();
    }
    
    private NumberScannerResponse scanNumber() {
        if (this.getCurrent() != '0') {
            this.base = Base.DECIMAL;
            return this.scanNumberBody();
        }
        this.consume();
        this.checkForType();
        this.checkForBase();
        if (this.base == null) {
            this.base = Base.DECIMAL;
            if (this.desiredKind == null) {
                this.index--;
            }
        }
        return this.scanNumberBody();
    }
    
    private NumberScannerResponse scanNumberBody() {
        CharPredicate isDigit = this.getPredicate();
        this.wholePart = this.consumeDigitRun(isDigit);
        
        if (this.isCurrentPeriod()) {
            this.consume();
            this.decimalPart = this.consumeDigitRun(isDigit);
        }
        
        Optional<Exponent> hasExponent = this.isCurrentExponent();
        if (hasExponent.isPresent()) {
            this.consume();
            this.exponentType = hasExponent.get();
            
            String sign = "";
            if (Chars.isSignSymbol(this.getCurrent())) {
                sign = this.getCurrentAsString();
                this.consume();
            }
            this.exponentPart = sign + this.consumeDigitRun(Chars::isDecDigit);
        }
        
        this.computeValue();
        this.resolveKind();
        
        Token.NumberLiteral.Unresolved numberLiteral = new Token.NumberLiteral.Unresolved(this.beginIndex, this.text.substring(this.beginIndex, this.index), this.value, this.kind);
        return new NumberScannerResponse(numberLiteral, this.index);
    }
    
    private CharPredicate getPredicate() {
        return switch (this.base) {
            case HEXADECIMAL -> Chars::isHexDigit;
            case DECIMAL -> Chars::isDecDigit;
            case OCTAL -> Chars::isOctDigit;
            case BINARY -> Chars::isBinDigit;
        };
    }
    
    private void computeValue() {
        int radix = this.base.base;
        
        BigInteger wholeInt = new BigInteger(this.wholePart, radix);
        BigDecimal tempValue = new BigDecimal(wholeInt);
        
        if (this.decimalPart != null) {
            BigInteger decimalInt = new BigInteger(this.decimalPart, radix);
            BigDecimal scale = BigDecimal.valueOf(radix).pow(this.decimalPart.length());
            
            BigDecimal fractional = new BigDecimal(decimalInt).divide(scale, MathContext.UNLIMITED);
            tempValue = tempValue.add(fractional);
        }
        
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
            if (! isIntegral && ! Token.DataTypeKind.Number.isFloatingPoint(this.desiredKind)) {
                throw new IllegalArgumentException("Value " + value + " has a fractional result but " + this.desiredKind + " can't hold decimals at " + this.index);
            }
            this.kind = this.desiredKind;
            return;
        }
        
        this.kind = isIntegral ? Token.DataTypeKind.Number.INT32 : Token.DataTypeKind.Number.FLOAT64;
    }
    
    private String consumeDigitRun(CharPredicate isDigit) {
        if (! this.hasCurrent() || isDigit.negate().test(getCurrent())) {
            throw new IllegalArgumentException("Expected digit at " + this.index);
        }
        if (this.getCurrent() == '_') {
            throw new IllegalArgumentException("Illegal underscore placement at " + this.index);
        }
        
        StringBuilder sb = new StringBuilder();
        boolean expectNumberAfterUnderscore = false;
        while (this.hasCurrent()) {
            char cc = this.getCurrent();
            
            if (isDigit.test(cc)) {
                expectNumberAfterUnderscore = false;
                sb.append(cc);
                this.consume();
            } else if (cc == '_') {
                expectNumberAfterUnderscore = true;
                this.consume(); // skip separator; loop consumes the digit right after it
            } else {
                if (expectNumberAfterUnderscore) {
                    throw new IllegalArgumentException("Illegal underscore placement at " + this.index);
                }
                return sb.toString();
            }
        }
        if (expectNumberAfterUnderscore) {
            throw new IllegalArgumentException("Illegal underscore placement at " + this.index);
        }
        return sb.toString();
    }
    
    private void checkForType() {
        if(! this.hasRun(3)) {return;}
        Optional<Token.DataTypeKind.Number> chosenKind = Token.DataTypeKind.Number.getByPrefix(this.text.substring(this.index, this.index + 3));
        if (chosenKind.isPresent()) {
            this.desiredKind = chosenKind.get();
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
}
