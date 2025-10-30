



package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SellerTest {
    private Seller seller;
    private Product necessityProduct;
    private Product luxuryProduct;
    private Buyer buyer;

    @BeforeEach
    void setUp() {
        seller = new Seller(0.2, false); 
        necessityProduct = new Product("Chleb", Product.Type.NECESSITY, 2.0, 10);
        luxuryProduct = new Product("Telewizor", Product.Type.LUXURY, 1000.0, 2);
        buyer = new Buyer(1000.0, 100.0, 0.3);
    }

    @Test
    void testAddProductOffer() {
        seller.addProductOffer(necessityProduct, 50);
        List<ProductOffer> offers = seller.getOffers();

        assertEquals(1, offers.size());
        ProductOffer offer = offers.get(0);
        assertEquals(necessityProduct, offer.getProduct());
        assertEquals(2.0 * 1.2, offer.getPrice(), 0.001); 
        assertEquals(50, offer.getAvailableQuantity());
    }

    @Test
    void testSellProductSuccess() {
        seller.addProductOffer(necessityProduct, 50);
        boolean result = seller.sellProduct(necessityProduct, 10, buyer);

        assertTrue(result);
        assertEquals(40, seller.getOffers().get(0).getAvailableQuantity());
        assertEquals(10 * (2.0 * 1.2 - 2.0), seller.getTotalProfit(), 0.001); 
    }

    @Test
    void testSellProductFailureNotEnoughQuantity() {
        seller.addProductOffer(necessityProduct, 5);
        boolean result = seller.sellProduct(necessityProduct, 10, buyer);

        assertFalse(result);
        assertEquals(5, seller.getOffers().get(0).getAvailableQuantity());
        assertEquals(0, seller.getTotalProfit(), 0.001);
    }

    @Test
    void testSellProductFailureProductNotOffered() {
        boolean result = seller.sellProduct(luxuryProduct, 1, buyer);
        assertFalse(result);
    }

    @Test
    void testPayInflationTax() {
        seller.addProductOffer(necessityProduct, 50);
        seller.sellProduct(necessityProduct, 10, buyer);

        double initialProfit = seller.getTotalProfit();

        assertEquals(initialProfit, seller.getTotalProfit(), 0.001);
    }

    @Test
    void testSetProfitMargin() {
        seller.addProductOffer(necessityProduct, 50);
        double initialPrice = seller.getOffers().get(0).getPrice();

        seller.setProfitMargin(0.3); 

        assertEquals(0.3, seller.getProfitMargin(), 0.001);
        assertEquals(necessityProduct.getProductionCost() * (1 + 0.3),
                seller.getOffers().get(0).getPrice(),
                0.001);
    }

    @Test
    void testTransactionLog() {
        seller.addProductOffer(necessityProduct, 50);
        seller.sellProduct(necessityProduct, 10, buyer);

        List<String> log = seller.getTransactionLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("Transakcja"));
        assertTrue(log.get(0).contains("kupił 10 szt. Chleb"));
    }


}