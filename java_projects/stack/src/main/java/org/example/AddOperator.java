package org.example;

public class AddOperator implements Operator {
    @Override
    public int apply(int... args) {
        if (args.length != 2) {
            throw new UnsupportedOperationException("Operator dodawania wymaga dwóch argumentów");
        }
        return args[0] + args[1];
    }
    @Override
    public int getRequiredArguments() {
        return 2; 
    }
}
