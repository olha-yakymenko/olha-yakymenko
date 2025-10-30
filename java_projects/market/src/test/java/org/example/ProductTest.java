package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithCorrectValues() {
        Product product = new Product("Mleko", Product.Type.NECESSITY, 2.5, 15);

        assertEquals("Mleko", product.getName());
        assertEquals(Product.Type.NECESSITY, product.getType());
        assertEquals(2.5, product.getProductionCost(), 0.001);
        assertEquals(15, product.getBaseDemand());
    }

    @Test
    void toStringShouldReturnFormattedString() {
        Product product = new Product("Chleb", Product.Type.NECESSITY, 2.0, 10);
        String expected = "Chleb (NECESSITY, koszt: 2.0)";

        assertEquals(expected, product.toString());
    }

    @Test
    void shouldDifferentiateProductTypes() {
        Product necessity = new Product("Mleko", Product.Type.NECESSITY, 2.5, 15);
        Product luxury = new Product("Telewizor", Product.Type.LUXURY, 1500.0, 2);

        assertNotEquals(necessity.getType(), luxury.getType());
    }
}