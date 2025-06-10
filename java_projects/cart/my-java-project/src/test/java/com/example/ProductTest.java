package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testConstructorAndGetters() {
        Product product = new Product("P001", "Laptop", 2000.0);
        assertEquals("P001", product.getCode());
    }

    @Test
    void testConstructorAndGettersName() {
        Product product = new Product("P001", "Laptop", 2000.0);
        assertEquals("Laptop", product.getName());
    }

    @Test
    void testConstructorAndGettersPrice() {
        Product product = new Product("P001", "Laptop", 2000.0);
        assertEquals(2000.0, product.getPrice());
    }

    @Test
    void testConstructorAndGettersDiscountPrice() {
        Product product = new Product("P001", "Laptop", 2000.0);
        assertEquals(2000.0, product.getDiscountPrice());
    }

    @Test
    void testSetDiscountPrice() {
        Product product = new Product("P001", "Laptop", 2000.0);
        product.setDiscountPrice(1500.0);
        assertEquals(1500.0, product.getDiscountPrice());
    }

    @Test
    void testCompareToByPrice() {
        Product product1 = new Product("P001", "Laptop", 2000.0);
        Product product2 = new Product("P002", "Phone", 1000.0);
        assertTrue(product1.compareTo(product2) > 0);
    }

    @Test
    void testCompareToByPriceReversed() {
        Product product1 = new Product("P001", "Laptop", 2000.0);
        Product product2 = new Product("P002", "Phone", 1000.0);
        assertTrue(product2.compareTo(product1) < 0);
    }

    @Test
    void testCompareToByNameWhenPricesAreEqual() {
        Product product1 = new Product("P001", "Laptop", 2000.0);
        Product product2 = new Product("P002", "Phone", 2000.0);
        assertTrue(product1.compareTo(product2) < 0); // "Laptop" powinien być przed "Phone"
    }

    @Test
    void testCompareToByNameWhenPricesAreEqualReversed() {
        Product product1 = new Product("P001", "Phone", 2000.0);
        Product product2 = new Product("P002", "Laptop", 2000.0);
        assertTrue(product2.compareTo(product1) < 0); // "Laptop" powinien być przed "Phone"
    }
}
