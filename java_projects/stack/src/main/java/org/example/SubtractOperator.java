package org.example;

public class SubtractOperator implements Operator {
    @Override
    public int apply(int ...args) {
        if (args.length !=2){
            throw new UnsupportedOperationException("Odejmowanie wymaga 2 argumenty");
        }
        int a=args[0];
        int b=args[1];
        return a - b;
    }
    @Override
        public int getRequiredArguments() {
            return 2; 
        }
}
