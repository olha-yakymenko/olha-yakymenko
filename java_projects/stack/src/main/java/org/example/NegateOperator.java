package org.example;

class NegateOperator implements Operator {
    @Override
    public int apply(int ...args) { 
        if (args.length !=1){
            throw new UnsupportedOperationException("Negacja wymaga tylko 1 argument");
        }
        int a=args[0];
        return -a;
     }
     @Override
        public int getRequiredArguments() {
            return 1; 
        }
}

