package org.example;

import java.util.*;

public class RPNCalculator {

    private final Map<String, Operator> operators = new HashMap<>();

    public RPNCalculator() {
        operators.put("+", new AddOperator());
        operators.put("-", new SubtractOperator());
        operators.put("*", new MultiplyOperator());
    }

    public int evaluate(String expression) {
        Stack stack = new Stack();  
        String[] tokens = expression.split(" ");
    
        for (String token : tokens) {
            if (operators.containsKey(token)) {
                Operator operator = operators.get(token);
    
                int[] args = new int[operator.getRequiredArguments()];
                
                for (int i = args.length - 1; i >= 0; i--) {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Za mało argumentów dla operatora: " + token);
                    }
                    String strValue = stack.pop();
                    try {
                        args[i] = Integer.parseInt(strValue);  
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Niepoprawna liczba: " + strValue);
                    }
                }
    
                stack.push(String.valueOf(operator.apply(args))); 
            } else {
                try {
                    stack.push(String.valueOf(Integer.parseInt(token))); 
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Nieznany token: " + token);
                }
            }
        }
    
        return Integer.parseInt(stack.pop());  
    }
    
    public void addOperator(String symbol, Operator operator) {
        operators.put(symbol, operator);
    }
}
