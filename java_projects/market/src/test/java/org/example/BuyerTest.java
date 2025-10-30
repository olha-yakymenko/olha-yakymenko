package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BuyerTest {

    private Buyer buyer;
    private Product chleb;
    private Product perfumy;
    private Seller seller;

    @BeforeEach
    void setUp() {
        buyer = new Buyer(100.0, 50.0, 1.0); 
        chleb = new Product("Chleb", Product.Type.NECESSITY, 2.0, 10);
        perfumy = new Product("Perfumy", Product.Type.LUXURY, 10.0, 3);

        buyer.addNeed(chleb, 10);
        buyer.addNeed(perfumy, 5);

        seller = new Seller(0.5, false); 
        seller.addProductOffer(chleb, 20); 
        seller.addProductOffer(perfumy, 10);

        buyer.observeSellers(List.of(seller));
    }

    @Test
    void testAddNeedStoresCorrectly() {
        Map<Product, Integer> needs = buyer.getNeeds();
        assertTrue(needs.containsKey(chleb));
        assertTrue(needs.containsKey(perfumy));
        assertEquals(10, needs.get(chleb));
        assertEquals(5, needs.get(perfumy));
    }

    @Test
    void testInitialBudgetAndIncomeUpdate() {
        assertEquals(100.0, buyer.getBudget());
        buyer.makePurchaseDecisions(); 
        assertTrue(buyer.getBudget() <= 150.0);
    }

    @Test
    void testMakePurchaseNecessity() {
        buyer.makePurchaseDecisions();
        List<String> history = buyer.getPurchaseHistory();
        assertTrue(history.stream().anyMatch(log -> log.contains("Chleb")));
    }

    @Test
    void testMakePurchaseLuxuryBudgetConstraint() {
        Product luksus = new Product("Zegarek", Product.Type.LUXURY, 40.0, 1);
        buyer.addNeed(luksus, 3);
        seller.addProductOffer(luksus, 1); 

        buyer.makePurchaseDecisions();

        assertFalse(buyer.getPurchaseHistory().stream().anyMatch(log -> log.contains("Zegarek")));
    }

    @Test
    void testPurchaseOnPriceDrop() {
        ProductOffer cheapOffer = new ProductOffer(chleb, 2.5, 10); 
        buyer.update(cheapOffer); 

        assertTrue(buyer.getPurchaseHistory().stream().anyMatch(log -> log.contains("Chleb")));
    }

    @Test
    void testNoPurchaseOnBigPriceChange() {
        buyer.priceHistory.put(chleb, 3.0);
        ProductOffer similarOffer = new ProductOffer(chleb, 5.95, 10);
        buyer.update(similarOffer);

        System.out.println(buyer.getPurchaseHistory());

        assertTrue(buyer.getPurchaseHistory().isEmpty());
    }

    @Test
    void testUpdateInflationChangesState() {
        buyer.update(0.07); 
        assertEquals(0.07, buyer.currentInflation, 0.0001);
    }


    @Test
    void testToStringFormat() {
        String desc = buyer.toString();
        assertTrue(desc.contains("Kupujący"));
        assertTrue(desc.contains("budżet"));
    }
}
