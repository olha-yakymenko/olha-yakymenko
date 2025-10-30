////package org.example;
////
////import java.util.ArrayList;
////import java.util.List;
////
////class MarketModel {
////    private final List<Seller> sellers = new ArrayList<>();
////    private final List<Buyer> buyers = new ArrayList<>();
////    private final CentralBank centralBank;
////    private int currentTurn = 0;
////    private final boolean detailedLogging;
////
////    public MarketModel(double initialInflation, boolean detailedLogging) {
////        this.centralBank = new CentralBank(initialInflation, detailedLogging);
////        this.detailedLogging = detailedLogging;
////    }
////
////    public void addSeller(Seller seller) {
////        sellers.add(seller);
////        centralBank.addObserver(seller);
////        if (detailedLogging) {
////            System.out.println("Dodano sprzedawcę z marżą: " + seller.getProfitMargin());
////        }
////    }
////
////    public void addBuyer(Buyer buyer) {
////        buyers.add(buyer);
////        centralBank.addObserver(buyer);
////        sellers.forEach(s -> buyer.addObserver(s));
////        if (detailedLogging) {
////            System.out.println("Dodano kupującego z budżetem: " + buyer.getBudget());
////        }
////    }
////
////
////    private void restockProducts() {
////        sellers.forEach(seller -> {
////            seller.getOffers().forEach(offer -> {
////                // Generuj losową liczbę od 1 do 50
////                int restockAmount = (int)(1 + Math.random() * 50);
////                offer.setAvailableQuantity(offer.getAvailableQuantity() + restockAmount);
////
////                System.out.println("Uzupełniono zapasy " + offer.getProduct().getName() +
////                        " o " + restockAmount + " szt.");
////            });
////        });
////    }
////
////    public void nextTurn() {
////        currentTurn++;
////        System.out.println("\n=== TURA " + currentTurn + " ===");
////
////        // 1. Bank oblicza nową inflację
////        centralBank.calculateInflation(sellers, buyers);
////        restockProducts();
////        // 2. Sprzedawcy aktualizują ceny
////        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation());
////        sellers.forEach(seller -> seller.accept(priceVisitor));
////
////        // 3. Kupujący podejmują decyzje zakupowe
////        buyers.forEach(buyer -> buyer.makePurchaseDecisions(sellers));
//////        PriceUpdateVisitor visitor = new PriceUpdateVisitor(centralBank.getInflation());
//////        buyers.forEach(buyer -> buyer.accept(visitor));
////        // 4. Bank pobiera podatek inflacyjny
////        centralBank.collectInflationTax(sellers);
////
////        logMarketStatus();
////
////        if (centralBank.checkStability()) {
////            System.out.println("SYSTEM OSIĄGNĄŁ STABILNOŚĆ W TURZE " + currentTurn);
////            printFinalReport();
////        }
////    }
////
////    private void logMarketStatus() {
////        System.out.println("\nSTATUS RYNKU:");
////        System.out.println("Liczba sprzedawców: " + sellers.size());
////        System.out.println("Liczba kupujących: " + buyers.size());
////
////        System.out.println("\nDOSTĘPNE PRODUKTY:");
////        sellers.forEach(seller -> {
////            seller.getOffers().forEach(offer -> {
////                System.out.println("- " + offer.getProduct().getName() +
////                        " (typ: " + offer.getProduct().getType() +
////                        "), cena: " + String.format("%.2f", offer.getPrice()) +
////                        ", dostępność: " + offer.getAvailableQuantity());
////            });
////        });
////
////        System.out.println("\nZYSKI SPRZEDAWCÓW:");
////        sellers.forEach(seller -> {
////            System.out.println("- " + seller + ": zysk = " + String.format("%.2f", seller.getTotalProfit()));
////        });
////
////        System.out.println("\nBUDŻETY KUPUJĄCYCH:");
////        buyers.forEach(buyer -> {
////            System.out.println("- " + buyer + ": budżet = " + String.format("%.2f", buyer.getBudget()));
////        });
////    }
////
////    private void printFinalReport() {
////        System.out.println("\n=== RAPORT KOŃCOWY ===");
////        System.out.println("Osiągnięto stabilność po " + currentTurn + " turach");
////        System.out.println("Końcowa inflacja: " + String.format("%.2f", centralBank.getInflation() * 100) + "%");
////
////        double totalSales = sellers.stream().mapToDouble(Seller::getTotalProfit).sum();
////        System.out.println("Łączny zysk sprzedawców: " + String.format("%.2f", totalSales));
////
////        double totalBudget = buyers.stream().mapToDouble(Buyer::getBudget).sum();
////        System.out.println("Łączny budżet kupujących: " + String.format("%.2f", totalBudget));
////
////        System.out.println("\nTRANSAKCJE:");
////        sellers.forEach(seller -> {
////            seller.getTransactionLog().forEach(System.out::println);
////        });
////
////        System.out.println("\nHISTORIA INFLACJI:");
////        centralBank.getInflationHistory().forEach(System.out::println);
////    }
////
////    public CentralBank getCentralBank() {
////        return centralBank;
////    }
////
////    public List<Seller> getSellers() {
////        return new ArrayList<>(sellers);
////    }
////
////    public int getCurrentTurn() {
////        return currentTurn;
////    }
////
////    public List<Buyer> getBuyers() {
////        return new ArrayList<>(buyers);
////    }
////
////
////
////}
//
//
//
//
//
//
//
//
//
////
////package org.example;
////
////import java.util.ArrayList;
////import java.util.List;
////
////class MarketModel {
////    private final List<Seller> sellers = new ArrayList<>();
////    private final List<Buyer> buyers = new ArrayList<>();
////    private final CentralBank centralBank;
////    private int currentTurn = 0;
////    private final boolean detailedLogging;
////
////    public MarketModel(double initialInflation, boolean detailedLogging) {
////        this.centralBank = new CentralBank(initialInflation);
////        this.detailedLogging = detailedLogging;
////    }
////
////    public void addSeller(Seller seller) {
////        sellers.add(seller);
////        centralBank.addObserver(seller);
////        if (detailedLogging) {
////            System.out.println("Dodano sprzedawcę z marżą: " + seller.getProfitMargin());
////        }
////    }
////
////    public void addBuyer(Buyer buyer) {
////        buyers.add(buyer);
////        buyer.addObserver((Observer) centralBank);
////        centralBank.addObserver(buyer);
////        buyer.observeSellers(sellers); // Zmienione
////        if (detailedLogging) {
////            System.out.println("Dodano kupującego z budżetem: " + buyer.getBudget());
////        }
////    }
////
////
////    private void restockProducts() {
////        sellers.forEach(seller -> {
////            seller.getOffers().forEach(offer -> {
////                int restockAmount = (int)(1 + Math.random() * 50);
////                offer.setAvailableQuantity(offer.getAvailableQuantity() + restockAmount);
////                System.out.println("Uzupełniono zapasy " + offer.getProduct().getName() +
////                        " o " + restockAmount + " szt.");
////            });
////        });
////    }
////
////    public void nextTurn() {
////        currentTurn++;
////        System.out.println("\n=== TURA " + currentTurn + " ===");
////
////        // 1. Bank oblicza nową inflację
////        centralBank.calculateInflation(sellers);
////        if (currentTurn==1){
////            restockProducts();
////        }
//////        restockProducts();
////        sellers.forEach(s -> s.restockProducts());
////
////        // 2. Sprzedawcy aktualizują ceny
////        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation());
////        sellers.forEach(seller -> seller.accept(priceVisitor));
////        buyers.forEach(buyer -> buyer.accept(priceVisitor));
////
////        // 3. Kupujący podejmują decyzje zakupowe
////        buyers.forEach(Buyer::makePurchaseDecisions); // Zmienione
////
////        // 4. Bank pobiera podatek inflacyjny
//////        centralBank.collectInflationTax(sellers);
////
////        logMarketStatus();
////
////        if (centralBank.checkStability()) {
////            System.out.println("SYSTEM OSIĄGNĄŁ STABILNOŚĆ W TURZE " + currentTurn);
////            printFinalReport();
////        }
////    }
////
////    private void logMarketStatus() {
////        System.out.println("\nSTATUS RYNKU:");
////        System.out.println("Liczba sprzedawców: " + sellers.size());
////        System.out.println("Liczba kupujących: " + buyers.size());
////
////        System.out.println("\nDOSTĘPNE PRODUKTY:");
////        sellers.forEach(seller -> {
////            seller.getOffers().forEach(offer -> {
////                System.out.println("- " + offer.getProduct().getName() +
////                        " (typ: " + offer.getProduct().getType() +
////                        "), cena: " + String.format("%.2f", offer.getPrice()) +
////                        ", dostępność: " + offer.getAvailableQuantity());
////            });
////        });
////
////        System.out.println("\nZYSKI SPRZEDAWCÓW:");
////        sellers.forEach(seller -> {
////            System.out.println("- " + seller + ": zysk = " + String.format("%.2f", seller.getTotalProfit()));
////        });
////
////        System.out.println("\nBUDŻETY KUPUJĄCYCH:");
////        buyers.forEach(buyer -> {
////            System.out.println("- " + buyer + ": budżet = " + String.format("%.2f", buyer.getBudget()));
////        });
////    }
////
////    private void printFinalReport() {
////        System.out.println("\n=== RAPORT KOŃCOWY ===");
////        System.out.println("Osiągnięto stabilność po " + currentTurn + " turach");
////        System.out.println("Końcowa inflacja: " + String.format("%.2f", centralBank.getInflation() * 100) + "%");
////
////        double totalSales = sellers.stream().mapToDouble(Seller::getTotalProfit).sum();
////        System.out.println("Łączny zysk sprzedawców: " + String.format("%.2f", totalSales));
////
////        double totalBudget = buyers.stream().mapToDouble(Buyer::getBudget).sum();
////        System.out.println("Łączny budżet kupujących: " + String.format("%.2f", totalBudget));
////
////        System.out.println("\nTRANSAKCJE:");
////        sellers.forEach(seller -> {
////            seller.getTransactionLog().forEach(System.out::println);
////        });
////
////        System.out.println("\nHISTORIA INFLACJI:");
////        centralBank.getInflationHistory().forEach(System.out::println);
////    }
////
////    public CentralBank getCentralBank() {
////        return centralBank;
////    }
////
////    public List<Seller> getSellers() {
////        return new ArrayList<>(sellers);
////    }
////
////    public int getCurrentTurn() {
////        return currentTurn;
////    }
////
////    public List<Buyer> getBuyers() {
////        return new ArrayList<>(buyers);
////    }
////}
//
//
//
//
//
//
//
//
//
//// MarketModel.java
//package org.example;
//
//import java.util.ArrayList;
//import java.util.List;
//
//class MarketModel {
//    private final List<Seller> sellers = new ArrayList<>();
//    private final List<Buyer> buyers = new ArrayList<>();
//    private final CentralBank centralBank;
//    private int currentTurn = 0;
//    private final boolean detailedLogging;
//
//    public MarketModel(double initialInflation, boolean detailedLogging) {
//        this.centralBank = new CentralBank(initialInflation);
//        this.detailedLogging = detailedLogging;
//    }
//
//    public void addSeller(Seller seller) {
//        sellers.add(seller);
//        centralBank.addObserver(seller);
//        seller.addObserver(centralBank);
//        if (detailedLogging) {
//            System.out.println("Dodano sprzedawcę z marżą: " + seller.getProfitMargin());
//        }
//    }
//
//    public void addBuyer(Buyer buyer) {
//        buyers.add(buyer);
//        centralBank.addObserver(buyer);
//        buyer.observeSellers(sellers);
//        if (detailedLogging) {
//            System.out.println("Dodano kupującego z budżetem: " + buyer.getBudget());
//        }
//    }
//
//    public void nextTurn() {
//        currentTurn++;
//        System.out.println("\n=== TURA " + currentTurn + " ===");
//
//        // 1. Bank oblicza nową inflację
//        centralBank.calculateInflation(sellers);
//
//        // 2. Sprzedawcy uzupełniają zapasy
//        sellers.forEach(Seller::restockProducts);
//
//        // 3. Sprzedawcy aktualizują ceny
//        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation());
//        sellers.forEach(seller -> seller.accept(priceVisitor));
//
//        // 4. Kupujący podejmują decyzje zakupowe
//        buyers.forEach(Buyer::makePurchaseDecisions);
//
//        // 5. Bank pobiera podatki
////        centralBank.collectTaxes(sellers);
//
//        logMarketStatus();
//
//        if (centralBank.checkStability()) {
//            System.out.println("SYSTEM OSIĄGNĄŁ STABILNOŚĆ W TURZE " + currentTurn);
//            printFinalReport();
//        }
//    }
//
//    private void logMarketStatus() {
//        System.out.println("\nSTATUS RYNKU:");
//        System.out.println("Liczba sprzedawców: " + sellers.size());
//        System.out.println("Liczba kupujących: " + buyers.size());
//
//        System.out.println("\nDOSTĘPNE PRODUKTY:");
//        sellers.forEach(seller -> {
//            seller.getOffers().forEach(offer -> {
//                System.out.println("- " + offer.getProduct().getName() +
//                        " (typ: " + offer.getProduct().getType() +
//                        "), cena: " + String.format("%.2f", offer.getPrice()) +
//                        ", dostępność: " + offer.getAvailableQuantity());
//            });
//        });
//
//        System.out.println("\nZYSKI SPRZEDAWCÓW:");
//        sellers.forEach(seller -> {
//            System.out.println("- " + seller + ": zysk = " + String.format("%.2f", seller.getTotalProfit()));
//        });
//
//        System.out.println("\nBUDŻETY KUPUJĄCYCH:");
//        buyers.forEach(buyer -> {
//            System.out.println("- " + buyer + ": budżet = " + String.format("%.2f", buyer.getBudget()));
//        });
//    }
//
//    private void printFinalReport() {
//        System.out.println("\n=== RAPORT KOŃCOWY ===");
//        System.out.println("Osiągnięto stabilność po " + currentTurn + " turach");
//        System.out.println("Końcowa inflacja: " + String.format("%.2f", centralBank.getInflation() * 100) + "%");
//
//        double totalSales = sellers.stream().mapToDouble(Seller::getTotalProfit).sum();
//        System.out.println("Łączny zysk sprzedawców: " + String.format("%.2f", totalSales));
//
//        double totalBudget = buyers.stream().mapToDouble(Buyer::getBudget).sum();
//        System.out.println("Łączny budżet kupujących: " + String.format("%.2f", totalBudget));
//
//        System.out.println("\nHISTORIA INFLACJI:");
//        centralBank.getInflationHistory().forEach(System.out::println);
//    }
//
//    public CentralBank getCentralBank() {
//        return centralBank;
//    }
//
//    public List<Seller> getSellers() {
//        return new ArrayList<>(sellers);
//    }
//
//    public List<Buyer> getBuyers() {
//        return new ArrayList<>(buyers);
//    }
//}














//package org.example;
//
//import java.util.ArrayList;
//import java.util.List;
//
//class MarketModel {
//    private final List<Seller> sellers = new ArrayList<>();
//    private final List<Buyer> buyers = new ArrayList<>();
//    private final CentralBank centralBank;
//    private int currentTurn = 0;
//    private final boolean detailedLogging;
//
//    public MarketModel(double initialInflation, boolean detailedLogging) {
//        this.centralBank = new CentralBank(initialInflation, detailedLogging);
//        this.detailedLogging = detailedLogging;
//    }
//
//    public void addSeller(Seller seller) {
//        sellers.add(seller);
//        centralBank.addObserver(seller);
//        if (detailedLogging) {
//            System.out.println("Dodano sprzedawcę z marżą: " + seller.getProfitMargin());
//        }
//    }
//
//    public void addBuyer(Buyer buyer) {
//        buyers.add(buyer);
//        centralBank.addObserver(buyer);
//        sellers.forEach(s -> buyer.addObserver(s));
//        if (detailedLogging) {
//            System.out.println("Dodano kupującego z budżetem: " + buyer.getBudget());
//        }
//    }
//
//
//    private void restockProducts() {
//        sellers.forEach(seller -> {
//            seller.getOffers().forEach(offer -> {
//                // Generuj losową liczbę od 1 do 50
//                int restockAmount = (int)(1 + Math.random() * 50);
//                offer.setAvailableQuantity(offer.getAvailableQuantity() + restockAmount);
//
//                System.out.println("Uzupełniono zapasy " + offer.getProduct().getName() +
//                        " o " + restockAmount + " szt.");
//            });
//        });
//    }
//
//    public void nextTurn() {
//        currentTurn++;
//        System.out.println("\n=== TURA " + currentTurn + " ===");
//
//        // 1. Bank oblicza nową inflację
//        centralBank.calculateInflation(sellers, buyers);
//        restockProducts();
//        // 2. Sprzedawcy aktualizują ceny
//        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation());
//        sellers.forEach(seller -> seller.accept(priceVisitor));
//
//        // 3. Kupujący podejmują decyzje zakupowe
//        buyers.forEach(buyer -> buyer.makePurchaseDecisions(sellers));
////        PriceUpdateVisitor visitor = new PriceUpdateVisitor(centralBank.getInflation());
////        buyers.forEach(buyer -> buyer.accept(visitor));
//        // 4. Bank pobiera podatek inflacyjny
//        centralBank.collectInflationTax(sellers);
//
//        logMarketStatus();
//
//        if (centralBank.checkStability()) {
//            System.out.println("SYSTEM OSIĄGNĄŁ STABILNOŚĆ W TURZE " + currentTurn);
//            printFinalReport();
//        }
//    }
//
//    private void logMarketStatus() {
//        System.out.println("\nSTATUS RYNKU:");
//        System.out.println("Liczba sprzedawców: " + sellers.size());
//        System.out.println("Liczba kupujących: " + buyers.size());
//
//        System.out.println("\nDOSTĘPNE PRODUKTY:");
//        sellers.forEach(seller -> {
//            seller.getOffers().forEach(offer -> {
//                System.out.println("- " + offer.getProduct().getName() +
//                        " (typ: " + offer.getProduct().getType() +
//                        "), cena: " + String.format("%.2f", offer.getPrice()) +
//                        ", dostępność: " + offer.getAvailableQuantity());
//            });
//        });
//
//        System.out.println("\nZYSKI SPRZEDAWCÓW:");
//        sellers.forEach(seller -> {
//            System.out.println("- " + seller + ": zysk = " + String.format("%.2f", seller.getTotalProfit()));
//        });
//
//        System.out.println("\nBUDŻETY KUPUJĄCYCH:");
//        buyers.forEach(buyer -> {
//            System.out.println("- " + buyer + ": budżet = " + String.format("%.2f", buyer.getBudget()));
//        });
//    }
//
//    private void printFinalReport() {
//        System.out.println("\n=== RAPORT KOŃCOWY ===");
//        System.out.println("Osiągnięto stabilność po " + currentTurn + " turach");
//        System.out.println("Końcowa inflacja: " + String.format("%.2f", centralBank.getInflation() * 100) + "%");
//
//        double totalSales = sellers.stream().mapToDouble(Seller::getTotalProfit).sum();
//        System.out.println("Łączny zysk sprzedawców: " + String.format("%.2f", totalSales));
//
//        double totalBudget = buyers.stream().mapToDouble(Buyer::getBudget).sum();
//        System.out.println("Łączny budżet kupujących: " + String.format("%.2f", totalBudget));
//
//        System.out.println("\nTRANSAKCJE:");
//        sellers.forEach(seller -> {
//            seller.getTransactionLog().forEach(System.out::println);
//        });
//
//        System.out.println("\nHISTORIA INFLACJI:");
//        centralBank.getInflationHistory().forEach(System.out::println);
//    }
//
//    public CentralBank getCentralBank() {
//        return centralBank;
//    }
//
//    public List<Seller> getSellers() {
//        return new ArrayList<>(sellers);
//    }
//
//    public int getCurrentTurn() {
//        return currentTurn;
//    }
//
//    public List<Buyer> getBuyers() {
//        return new ArrayList<>(buyers);
//    }
//
//
//
//}










package org.example;

import java.util.ArrayList;
import java.util.List;

class MarketModel {
    private final List<Seller> sellers = new ArrayList<>();
    private final List<Buyer> buyers = new ArrayList<>();
    private final CentralBank centralBank;
    private int currentTurn = 0;
    private final boolean detailedLogging;

    public MarketModel(double initialInflation, boolean detailedLogging) {
        this.centralBank = new CentralBank(initialInflation, detailedLogging);
        this.detailedLogging = detailedLogging;
    }

    public void addSeller(Seller seller) {
        sellers.add(seller);
        centralBank.addObserver(seller);
        seller.addObserver(centralBank);
        if (detailedLogging) {
            System.out.println("Dodano sprzedawcę z marżą: " + seller.getProfitMargin());
        }
    }

    public void addBuyer(Buyer buyer) {
        buyers.add(buyer);
        centralBank.addObserver(buyer);
        buyer.addObserver(centralBank);
        buyer.observeSellers(sellers); // Zmienione
        if (detailedLogging) {
            System.out.println("Dodano kupującego z budżetem: " + buyer.getBudget());
        }
    }

    private void restockProducts() {
        sellers.forEach(seller -> {
            seller.getOffers().forEach(offer -> {
                int restockAmount = (int)(1 + Math.random() * 50);
                offer.setAvailableQuantity(offer.getAvailableQuantity() + restockAmount);
                System.out.println("Uzupełniono zapasy " + offer.getProduct().getName() +
                        " o " + restockAmount + " szt.");
            });
        });
    }

    public void nextTurn() {
        currentTurn++;
        System.out.println("\n=== TURA " + currentTurn + " ===");

        // 1. Bank oblicza nową inflację
        centralBank.calculateInflation(sellers, buyers);
        restockProducts();

        sellers.forEach(s -> s.setProfitMargin());
        sellers.forEach(s -> s.zeroTotal());;
        // 2. Sprzedawcy aktualizują ceny
        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation());
        sellers.forEach(seller -> seller.accept(priceVisitor));
        buyers.forEach(buyer -> buyer.accept(priceVisitor));

        // 3. Kupujący podejmują decyzje zakupowe
        buyers.forEach(Buyer::makePurchaseDecisions); // Zmienione

        // 4. Bank pobiera podatek inflacyjny
        centralBank.collectInflationTax(sellers);


        logMarketStatus();

        sellers.forEach(s -> s.zeroTotalProfit());

        if (centralBank.checkStability() && buyers.stream().allMatch(b -> b.hasSatisfiedNeeds())) {
            System.out.println("SYSTEM OSIĄGNĄŁ STABILNOŚĆ W TURZE " + currentTurn);
            printFinalReport();
        }
    }

    private void logMarketStatus() {
        System.out.println("\nSTATUS RYNKU:");
        System.out.println("Liczba sprzedawców: " + sellers.size());
        System.out.println("Liczba kupujących: " + buyers.size());

        System.out.println("\nDOSTĘPNE PRODUKTY:");
        sellers.forEach(seller -> {
            seller.getOffers().forEach(offer -> {
                System.out.println("- " + offer.getProduct().getName() +
                        " (typ: " + offer.getProduct().getType() +
                        "), cena: " + String.format("%.2f", offer.getPrice()) +
                        ", dostępność: " + offer.getAvailableQuantity());
            });
        });

        System.out.println("\nZYSKI SPRZEDAWCÓW:");
        sellers.forEach(seller -> {
            System.out.println("- " + seller + ": zysk = " + String.format("%.2f", seller.getTotalProfit()));
        });

        System.out.println("\nBUDŻETY KUPUJĄCYCH:");
        buyers.forEach(buyer -> {
            System.out.println("- " + buyer + ": budżet = " + String.format("%.2f", buyer.getBudget()));
        });
    }

    private void printFinalReport() {
        System.out.println("\n=== RAPORT KOŃCOWY ===");
        System.out.println("Osiągnięto stabilność po " + currentTurn + " turach");
        System.out.println("Końcowa inflacja: " + String.format("%.2f", centralBank.getInflation() * 100) + "%");

        double totalSales = sellers.stream().mapToDouble(Seller::getTotalProfit).sum();
        System.out.println("Łączny zysk sprzedawców: " + String.format("%.2f", totalSales));

        double totalBudget = buyers.stream().mapToDouble(Buyer::getBudget).sum();
        System.out.println("Łączny budżet kupujących: " + String.format("%.2f", totalBudget));

        System.out.println("\nTRANSAKCJE:");
        sellers.forEach(seller -> {
            seller.getTransactionLog().forEach(System.out::println);
        });

        System.out.println("\nHISTORIA INFLACJI:");
        centralBank.getInflationHistory().forEach(System.out::println);
    }

    public CentralBank getCentralBank() {
        return centralBank;
    }

    public List<Seller> getSellers() {
        return new ArrayList<>(sellers);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public List<Buyer> getBuyers() {
        return new ArrayList<>(buyers);
    }
}