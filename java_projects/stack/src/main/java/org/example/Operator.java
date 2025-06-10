package org.example;

public interface Operator {
    int apply(int... args);
    default int getRequiredArguments() {
        return 2; 
    }
}
