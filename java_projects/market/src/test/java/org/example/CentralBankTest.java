
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class CentralBankTest {
    private CentralBank bank;
    private Seller seller;
    private Product product;

    @BeforeEach
    void setUp() {
        bank = new CentralBank(0.05, false); 
        seller = new Seller(0.1, false); 
        product = new Product("Chleb", Product.Type.NECESSITY, 2.0,  3);
        seller.addProductOffer(product, 100); 
    }

    @Test
    void testInitialInflation() {
        assertEquals(0.05, bank.getInflation(), 0.001);
    }

    @Test
    void testTaxCollection() {
        bank.collectInflationTax(List.of(seller));
        assertTrue(bank.getHistoricalTaxRevenues().isEmpty()); // Nie ma jeszcze historii
    }

    @Test
    void testInflationIncreaseWhenTaxRevenueLow() {
        bank.setTargetTaxRevenue(1000); 
        bank.calculateInflation(List.of(seller), List.of()); // Wpływy będą niskie (~44)
        assertTrue(bank.getInflation() > 0.05, "Inflacja powinna wzrosnąć przy niskich wpływach");
    }

    @Test
    void testInflationDecreaseWhenTaxRevenueHigh() {
        bank.setTargetTaxRevenue(10); 
        bank.calculateInflation(List.of(seller), List.of()); // Wpływy będą wyższe (~44)
        assertTrue(bank.getInflation() < 0.05, "Inflacja powinna spaść przy wysokich wpływach");
    }

    @Test
    void testPriceChangeImpactOnInflation() {
        ProductOffer offer = seller.getOffers().get(0);
        offer.setPrice(offer.getPrice() * 1.2);

        bank.calculateInflation(List.of(seller), List.of());
        assertTrue(bank.getInflation() > 0.05, "Inflacja powinna wzrosnąć przy wzroście cen");
    }

    @Test
    void testStabilityCheckWithGoodRevenue() {
        for (int i = 0; i < 5; i++) {
            bank.getHistoricalTaxRevenues().add(bank.getTargetTaxRevenue());
        }
        assertTrue(bank.checkStability(), "System powinien być stabilny");
    }

    @Test
    void testStabilityCheckWithBadRevenue() {
        for (int i = 0; i < 5; i++) {
            bank.getHistoricalTaxRevenues().add(bank.getTargetTaxRevenue() * 0.5);
        }
        assertFalse(bank.checkStability(), "System nie powinien być stabilny");
    }

    @Test
    void testObserverNotification() {
        Buyer buyer = new Buyer(100, 10, 0.5);
        bank.addObserver(buyer);
        bank.notifyObservers();
        assertEquals(bank.getInflation(), buyer.currentInflation, 0.001);
    }
}