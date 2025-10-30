package org.example;
import java.util.List;
public class MarketSimulation {
    public static void main(String[] args) {
        boolean detailedLogging = true;
        int numberofturns = 100;
        MarketModel model = new MarketModel(0.05, detailedLogging);

        // Tworzenie produktów
        Product bread = new Product("Chleb", Product.Type.NECESSITY, 2.0, 3);
        Product milk = new Product("Mleko", Product.Type.NECESSITY, 1.5, 2);
        Product tv = new Product("Telewizor", Product.Type.LUXURY, 150.0, 0);
        Product phone = new Product("Smartfon", Product.Type.LUXURY, 80.0, 0);

        // Dodawanie sprzedawców
        Seller seller1 = new Seller(0.10, detailedLogging);
        seller1.addProductOffer(bread, 100);
        seller1.addProductOffer(milk, 80);
        model.addSeller(seller1);

        Seller seller2 = new Seller(0.15, detailedLogging);
        seller2.addProductOffer(tv, 10);
        seller2.addProductOffer(phone, 15);
        model.addSeller(seller2);

        // Dodawanie kupujących
        for (int i = 0; i < 5; i++) {
            Buyer buyer = new Buyer(500.0, 80.0, 0.7);
            buyer.addNeed(bread, bread.getBaseDemand());
            buyer.addNeed(milk, milk.getBaseDemand());
            if (i % 2 == 0) {
                buyer.addNeed(tv, 1);
            }
            if (i % 3 == 0) {
                buyer.addNeed(phone, 1);
            }

            model.addBuyer(buyer);
        }

        // Uruchomienie symulacji
        for (int i = 0; i < numberofturns; i++) {

            model.nextTurn();
        }

        List<Double> inflations = model.getCentralBank().getInflationPercentages();
        System.out.println(inflations);
        InflationPlotter.plotInflation(inflations);
    }
}