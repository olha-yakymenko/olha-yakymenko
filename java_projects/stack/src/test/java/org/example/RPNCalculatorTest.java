package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RPNCalculatorTest {

    @Test
    public void testAddition() {
        RPNCalculator calculator = new RPNCalculator();
        assertEquals(7, calculator.evaluate("3 4 +"));
    }

    @Test
    public void testSubtraction() {
        RPNCalculator calculator = new RPNCalculator();
        assertEquals(1, calculator.evaluate("5 4 -"));
    }

    @Test
    public void testMultiplication() {
        RPNCalculator calculator = new RPNCalculator();
        assertEquals(12, calculator.evaluate("3 4 *"));
    }


    @Test
    public void testComplexExpression() {
        RPNCalculator calculator = new RPNCalculator();
        assertEquals(14, calculator.evaluate("3 4 + 2 *"));
    }

    @Test
    public void testBinaryOperatorWithInsufficientArguments() {
        RPNCalculator calculator = new RPNCalculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.evaluate("3 +");
        });
        assertEquals("Za mało argumentów dla operatora: +", exception.getMessage());
    }

    @Test
    public void testUnknownOperator() {
        RPNCalculator calculator = new RPNCalculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.evaluate("3 4 @");
        });
        assertEquals("Nieznany token: @", exception.getMessage());
    }

    @Test
    public void testIncorrectRPNSyntax() {
        RPNCalculator calculator = new RPNCalculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.evaluate("3 4 + +");
        });
        assertEquals("Za mało argumentów dla operatora: +", exception.getMessage());
    }

    @Test
    public void testDivideOperatorWithDivideOperatorAdded() {
        RPNCalculator calculator = new RPNCalculator();
        DivideOperator customDivideOperator = new DivideOperator();  
        calculator.addOperator("/", customDivideOperator);  
        
        assertEquals(4, calculator.evaluate("20 5 /"));  
    }



}
