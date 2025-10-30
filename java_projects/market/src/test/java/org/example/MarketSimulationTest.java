
package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketSimulationTest {

    @Test
    void mainMethodShouldRunWithoutErrors() {
        assertDoesNotThrow(() -> MarketSimulation.main(new String[]{}));
    }

    @Test
    void simulationShouldCompleteWithinReasonableTime() {
        long startTime = System.currentTimeMillis();
        MarketSimulation.main(new String[]{});
        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 5000, "Symulacja powinna zakończyć się w mniej niż 5 sekund");
    }

    @Test
    void simulationShouldProduceOutput() {
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            MarketSimulation.main(new String[]{});
            String output = outputStream.toString();

            assertFalse(output.isEmpty(), "Symulacja powinna generować output w konsoli");
            assertTrue(output.contains("TURA"), "Symulacja powinna zawierać logi dotyczące tur");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void simulationShouldReachStableState() {
        MarketModel model = new MarketModel(0.05, false);

        Product bread = new Product("Chleb", Product.Type.NECESSITY, 2.0, 3);
        Seller seller = new Seller(0.10, false);
        seller.addProductOffer(bread, 100);
        model.addSeller(seller);

        Buyer buyer = new Buyer(5000.0, 1000.0, 0.7);
        buyer.addNeed(bread, bread.getBaseDemand());
        model.addBuyer(buyer);

        for (int i = 0; i < 100; i++) {
            model.nextTurn();
            if (model.getCentralBank().checkStability()) {
                break;
            }
        }

        assertTrue(model.getCentralBank().checkStability(),
                "Symulacja powinna osiągnąć stan stabilności");

    }

    @Test
    void simulationShouldHandleMinimumParticipants() {
        MarketModel model = new MarketModel(0.05, false);

        Product product = new Product("Test", Product.Type.NECESSITY, 1.0, 1);
        Seller seller = new Seller(0.10, false);
        seller.addProductOffer(product, 1);
        model.addSeller(seller);

        Buyer buyer = new Buyer(100.0, 10.0, 0.7);
        buyer.addNeed(product, 1);
        model.addBuyer(buyer);

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                model.nextTurn();
            }
        }, "Symulacja powinna działać z minimalną liczbą uczestników");
    }


    @Test
    void multipleBuyersShouldStabilizeOverTime() {
        MarketModel model = new MarketModel(0.05, false);

        Product rice = new Product("Ryż", Product.Type.NECESSITY, 2.0, 4);
        Seller seller = new Seller(0.10, false);
        seller.addProductOffer(rice, 300);
        model.addSeller(seller);

        for (int i = 0; i < 5; i++) {
            Buyer buyer = new Buyer(500.0 + i * 100, 50.0, 0.5 + i * 0.1);
            buyer.addNeed(rice, 3);
            model.addBuyer(buyer);
        }

        for (int i = 0; i < 100; i++) {
            model.nextTurn();
            if (model.getCentralBank().checkStability()) break;
        }

        assertTrue(model.getCentralBank().checkStability(),
                "Rynek z wieloma kupującymi powinien osiągnąć stabilność");
    }

    @Test
    void luxuryProductsShouldNotDestabilizeTheMarket() {
        MarketModel model = new MarketModel(0.03, false);

        Product tv = new Product("Telewizor", Product.Type.LUXURY, 1000.0, 2);
        Seller seller = new Seller(0.20, false);
        seller.addProductOffer(tv, 50);
        model.addSeller(seller);

        Buyer buyer = new Buyer(3000.0, 500.0, 0.9);
        buyer.addNeed(tv, 1);
        model.addBuyer(buyer);

        for (int i = 0; i < 50; i++) {
            model.nextTurn();
            if (model.getCentralBank().checkStability()) break;
        }

        assertTrue(model.getCentralBank().checkStability(),
                "Luksusowe dobra nie powinny destabilizować rynku");
    }


    @Test
    void productPriceShouldStabilizeAroundInitialPrice() {
        MarketModel model = new MarketModel(0.05, false);

        Product sugar = new Product("Cukier", Product.Type.NECESSITY, 5.0, 2);
        Seller seller = new Seller(0.10, false);
        seller.addProductOffer(sugar, 100);
        model.addSeller(seller);

        Buyer buyer = new Buyer(500.0, 100.0, 0.6);
        buyer.addNeed(sugar, 2);
        model.addBuyer(buyer);

        double initialPrice = sugar.getProductionCost();
        double lastObservedPrice = initialPrice;

        for (int i = 0; i < 100; i++) {
            model.nextTurn();
            List<ProductOffer> offers = seller.getOffers();
            for (ProductOffer offer : offers) {
                if (offer.getProduct().equals(sugar)) {
                    lastObservedPrice = offer.getPrice();
                }
            }
        }

        double delta = Math.abs(lastObservedPrice - initialPrice);
        assertTrue(delta < 100.0, "Cena powinna ustabilizować się w rozsądnym zakresie (" + delta + ")");
    }
}
