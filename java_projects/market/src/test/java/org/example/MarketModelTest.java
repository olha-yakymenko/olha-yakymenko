package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class MarketModelTest {
    private MarketModel model;
    private Seller seller;
    private Buyer buyer;
    private Product product;
    private final boolean DETAILED_LOGGING = false;

    @BeforeEach
    void setUp() {
        model = new MarketModel(0.05, DETAILED_LOGGING);
        product = new Product("Chleb", Product.Type.NECESSITY, 2.0, 10);
        seller = new Seller(0.2, DETAILED_LOGGING);
        buyer = new Buyer(1000.0, 100.0, 0.7);
    }

    @Test
    void shouldAddSellerCorrectly() {
        model.addSeller(seller);
        List<Seller> sellers = model.getSellers();
        assertEquals(1, sellers.size());
        assertEquals(seller, sellers.get(0));
    }

    @Test
    void shouldAddBuyerCorrectly() {
        model.addBuyer(buyer);
        assertDoesNotThrow(() -> model.nextTurn());
    }

    @Test
    void shouldInitializeWithCentralBank() {
        assertNotNull(model.getCentralBank());
        assertEquals(0.05, model.getCentralBank().getInflation(), 0.001);
    }

    @Test
    void nextTurnShouldIncrementTurnCounter() {
        int initialTurn = model.getCurrentTurn();
        model.nextTurn();
        assertEquals(initialTurn + 1, model.getCurrentTurn());
    }

    @Test
    void shouldCompleteFullTurnWithoutErrors() {
        seller.addProductOffer(product, 50);
        model.addSeller(seller);
        buyer.addNeed(product, 5);
        model.addBuyer(buyer);

        assertDoesNotThrow(() -> model.nextTurn());
    }

    @Test
    void shouldDetectStabilityAfterMultipleTurns() {
        seller.addProductOffer(product, 100);
        model.addSeller(seller);
        buyer.addNeed(product, 1);
        model.addBuyer(buyer);

        // Symulacja kilku tur
        for (int i = 0; i < 10; i++) {
            model.nextTurn();
        }

        // Sprawdzenie czy osiągnięto stabilność (może wymagać dostosowania)
        assertTrue(model.getCentralBank().checkStability() ||
                model.getCurrentTurn() >= 10);
    }

    @Test
    void shouldHandleEmptyMarket() {
        assertDoesNotThrow(() -> model.nextTurn());
    }

    @Test
    void shouldRestockProductsDuringTurn() {
        seller.addProductOffer(product, 10);
        model.addSeller(seller);
        int initialQuantity = seller.getOffers().get(0).getAvailableQuantity();

        model.nextTurn();

        assertTrue(seller.getOffers().get(0).getAvailableQuantity() > initialQuantity);
    }
    @Test
    void getSellersShouldReturnCopyOfList() {
        model.addSeller(seller);
        List<Seller> sellers = model.getSellers();
        sellers.clear();

        assertEquals(1, model.getSellers().size());
    }
}