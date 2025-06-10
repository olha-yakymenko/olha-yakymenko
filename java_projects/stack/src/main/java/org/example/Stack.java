package org.example;
import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private String[] elements;
    private int topIndex;  

    public Stack() {
        elements = new String[10];
        topIndex = 0;  
    }

    public void push(String item) {
        if (item == null) {
            throw new IllegalArgumentException("Nie można dodać null do stosu");
        }
        ensureCapacity();
        elements[topIndex++] = item;  
    }
    
    public String pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        String item = elements[--topIndex];  
        elements[topIndex] = null;  
        return item;
    }

    public String peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements[topIndex - 1];  
    }

    public boolean isEmpty() {
        return topIndex == 0;  
    }

    private void ensureCapacity() {
        if (topIndex == elements.length) { 
            int newCapacity = elements.length + 10;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
}
