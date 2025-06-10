package org.example;

public class DivideOperator implements Operator {
    @Override
    public int apply(int... args) {
        if (args.length != 2) {
            throw new UnsupportedOperationException("Dzielenie wymaga dokładnie dwóch argumentów");
        }
        int a = args[1];
        int b = args[0];
        
        if (a == 0) {
            throw new ArithmeticException("Dzielenie przez zero");
        }
        
        return b / a;
    }
    @Override
        public int getRequiredArguments() {
            return 2; 
        }
}
