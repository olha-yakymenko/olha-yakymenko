package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductOfferTest {
    private Product product;
    private ProductOffer offer;

    @BeforeEach
    void setUp() {
        product = new Product("Chleb", Product.Type.NECESSITY, 2.0, 10);
        offer = new ProductOffer(product, 2.4, 50);
    }

    @Test
    void shouldCreateOfferWithCorrectValues() {
        assertEquals(product, offer.getProduct());
        assertEquals(2.4, offer.getPrice(), 0.001);
        assertEquals(50, offer.getAvailableQuantity());
    }

    @Test
    void shouldUpdatePrice() {
        offer.setPrice(2.6);
        assertEquals(2.6, offer.getPrice(), 0.001);
    }

    @Test
    void shouldUpdateQuantity() {
        offer.setAvailableQuantity(30);
        assertEquals(30, offer.getAvailableQuantity());
    }

    @Test
    void shouldDecreaseQuantity() {
        offer.decreaseQuantity(5);
        assertEquals(45, offer.getAvailableQuantity());
    }

    @Test
    void shouldNotAllowNegativeQuantity() {
        offer.decreaseQuantity(60);
        assertEquals(0, offer.getAvailableQuantity());
    }

    @Test
    void toStringShouldReturnFormattedString() {
        String expected = "Chleb - cena: 2.4, ilość: 50";
        assertEquals(expected, offer.toString());
    }

    @Test
    void shouldHandleZeroQuantity() {
        ProductOffer emptyOffer = new ProductOffer(product, 2.4, 0);
        assertEquals(0, emptyOffer.getAvailableQuantity());
    }
}