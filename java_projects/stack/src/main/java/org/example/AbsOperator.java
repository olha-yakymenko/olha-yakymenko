package org.example;

public class AbsOperator implements Operator {
    @Override
    public int apply(int... args) {
        if (args.length != 1) {
            throw new UnsupportedOperationException("AbsOperator wymaga jednego argumentu");
        }
        return Math.abs(args[0]);
    }
    @Override
    public int getRequiredArguments() {
        return 1; 
    }
}


