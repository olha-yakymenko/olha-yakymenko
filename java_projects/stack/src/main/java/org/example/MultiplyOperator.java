package org.example;

class MultiplyOperator implements Operator {
    @Override
    public int apply(int ...args) {
        if (args.length != 2) {
            throw new UnsupportedOperationException("Dzielenie wymaga dokładnie dwóch argumentów");
        }
        int a = args[0];
        int b = args[1];
        return a*b;
     }
     @Override
        public int getRequiredArguments() {
            return 2; 
        }
}
