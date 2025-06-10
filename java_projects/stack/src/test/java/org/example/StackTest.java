package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.EmptyStackException;

class StackTest {
    @Test
    void testPush(){
        Stack stack = new Stack();
        stack.push("1");
        assertEquals("1", stack.peek());
    }


    @Test
    void testPop() {
        Stack stack = new Stack();
        stack.push("element1");
        stack.push("element2");
        assertEquals("element2", stack.pop(), "Metoda pop powinna zwrócić ostatnio dodany element 'element2'");
        assertEquals("element1", stack.pop(), "Metoda pop powinna zwrócić 'element1'");
    }

    @Test
    void testPopOnEmptyStack() {
        Stack stack = new Stack();
        assertThrows(EmptyStackException.class, () -> stack.pop());

    }

    @Test
    void testPeek() {
        Stack stack = new Stack();
        stack.push("element1");
        assertEquals("element1", stack.peek(), "Metoda peek powinna zwrócić 'element1'");
        stack.push("element2");
        assertEquals("element2", stack.peek(), "Metoda peek powinna zwrócić 'element2'");
    }

    @Test
    void testPeekOnEmptyStack() {
        Stack stack = new Stack();
        assertThrows(EmptyStackException.class, () -> stack.pop());
    }

    @Test
    void testCheckEmpty() {
        Stack stack = new Stack();
        assertTrue(stack.isEmpty(), "Nowo utworzony stos powinien być pusty");
        stack.push("element1");
        assertFalse(stack.isEmpty(), "Stos nie powinien być pusty po dodaniu elementu");
        stack.pop();
        assertTrue(stack.isEmpty(), "Stos powinien być pusty po usunięciu wszystkich elementów");
    }
    @Test
    void testPushNullThrowsException() {
        Stack stack = new Stack();
        assertThrows(IllegalArgumentException.class, () -> stack.push(null), "Dodanie null powinno rzucić IllegalArgumentException");
    }
}
