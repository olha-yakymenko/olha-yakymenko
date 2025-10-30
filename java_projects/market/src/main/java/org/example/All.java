//package org.example;
//
//import java.util.*;
//
//interface Observer {
//    void update();
//}
//
//interface Observable {
//    void addObserver(Observer observer);
//    void removeObserver(Observer observer);
//    void notifyObservers();
//}
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
//        PriceUpdateVisitor priceVisitor = new PriceUpdateVisitor(centralBank.getInflation(), detailedLogging);
//        sellers.forEach(seller -> seller.accept(priceVisitor));
//
//        // 3. Kupujący podejmują decyzje zakupowe
//        buyers.forEach(buyer -> buyer.makePurchaseDecisions(sellers));
//
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
//        return Collections.unmodifiableList(sellers);
//    }
//
//    public List<Buyer> getBuyers() {
//        return Collections.unmodifiableList(buyers);
//    }
//
//    public int getCurrentTurn() {
//        return currentTurn;
//    }
//}
//
//class Product {
//    public enum Type { NECESSITY, LUXURY }
//
//    private final String name;
//    private final Type type;
//    private final double productionCost;
//    private final int baseDemand;
//
//    public Product(String name, Type type, double productionCost, int baseDemand) {
//        this.name = name;
//        this.type = type;
//        this.productionCost = productionCost;
//        this.baseDemand = baseDemand;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public Type getType() {
//        return type;
//    }
//
//    public double getProductionCost() {
//        return productionCost;
//    }
//
//    public int getBaseDemand() {
//        return baseDemand;
//    }
//
//    @Override
//    public String toString() {
//        return name + " (" + type + ", koszt: " + productionCost + ")";
//    }
//}
//
//class ProductOffer {
//    private final Product product;
//    private double price;
//    private int availableQuantity;
//
//    public ProductOffer(Product product, double price, int availableQuantity) {
//        this.product = product;
//        this.price = price;
//        this.availableQuantity = availableQuantity;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public int getAvailableQuantity() {
//        return availableQuantity;
//    }
//
//    public void setAvailableQuantity(int availableQuantity) {
//        this.availableQuantity = availableQuantity;
//    }
//
//    public void decreaseQuantity(int amount) {
//        this.availableQuantity -= amount;
//    }
//
//    @Override
//    public String toString() {
//        return product.getName() + " - cena: " + price + ", ilość: " + availableQuantity;
//    }
//}
//
//class Seller implements Observer {
//    private final List<ProductOffer> offers = new ArrayList<>();
//    private double profitMargin;
//    private double totalProfit = 0;
//    private final List<String> transactionLog = new ArrayList<>();
//    private final boolean detailedLogging;
//
//    public Seller(double initialProfitMargin, boolean detailedLogging) {
//        this.profitMargin = initialProfitMargin;
//        this.detailedLogging = detailedLogging;
//    }
//
//    public void addProductOffer(Product product, int initialQuantity) {
//        double initialPrice = product.getProductionCost() * (1 + profitMargin);
//        offers.add(new ProductOffer(product, initialPrice, initialQuantity));
//            System.out.println("Dodano produkt: " + product.getName() +
//                    ", cena: " + initialPrice +
//                    ", ilość: " + initialQuantity);
//
//    }
//
//    public List<ProductOffer> getOffers() {
//        return Collections.unmodifiableList(offers);
//    }
//
//    public void adjustPrices(double inflation) {
//        for (ProductOffer offer : offers) {
//            double oldPrice = offer.getPrice();
//            double newPrice = oldPrice * (1 + inflation) * (1 + profitMargin/100);
//            offer.setPrice(newPrice);
//
//                System.out.println("Dostosowanie ceny " + offer.getProduct().getName() +
//                        ": " + String.format("%.2f", oldPrice) + " -> " +
//                        String.format("%.2f", newPrice) +
//                        " (inflacja: " + String.format("%.2f", inflation * 100) +
//                        "%, marża: " + String.format("%.2f", profitMargin * 100) + "%)");
//
//        }
//    }
//
//    public boolean sellProduct(Product product, int quantity, Buyer buyer) {
//        Optional<ProductOffer> offerOpt = offers.stream()
//                .filter(o -> o.getProduct().equals(product))
//                .findFirst();
//
//        if (offerOpt.isPresent()) {
//            ProductOffer offer = offerOpt.get();
//            if (offer.getAvailableQuantity() >= quantity) {
//                double totalCost = quantity * offer.getPrice();
//                offer.decreaseQuantity(quantity);
//                double profit = quantity * (offer.getPrice() - offer.getProduct().getProductionCost());
//                totalProfit += profit;
//
//                String logEntry = String.format("Transakcja: %s kupił %d szt. %s za %.2f (zysk: %.2f)",
//                        buyer, quantity, product.getName(), totalCost, profit);
//                transactionLog.add(logEntry);
//
//                    System.out.println(logEntry);
//
//                return true;
//            }
//        }
//            System.out.println("Brak możliwości sprzedaży " + product.getName() + " dla " + buyer);
//
//        return false;
//    }
//
//    public void payInflationTax(double taxAmount) {
//        totalProfit -= taxAmount;
//            System.out.println(this + " zapłacił podatek inflacyjny: " +
//                    String.format("%.2f", taxAmount) +
//                    ", zysk po opodatkowaniu: " + String.format("%.2f", totalProfit));
//
//    }
//
//    public double getTotalProfit() {
//        return totalProfit;
//    }
//
//    public double getProfitMargin() {
//        return profitMargin;
//    }
//
//    public void setProfitMargin(double profitMargin) {
//        this.profitMargin = profitMargin;
//            System.out.println("Zmieniono marżę sprzedawcy na: " + String.format("%.2f", profitMargin * 100) + "%");
//
//    }
//
//    public List<String> getTransactionLog() {
//        return Collections.unmodifiableList(transactionLog);
//    }
//
//    @Override
//    public void update() {
//            System.out.println("Sprzedawca otrzymał powiadomienie o zmianie inflacji");
//
//    }
//
//    public void accept(PriceUpdateVisitor visitor) {
//        visitor.visit(this);
//    }
//
//    @Override
//    public String toString() {
//        return "Sprzedawca (marża: " + String.format("%.2f", profitMargin * 100) + "%)";
//    }
//}
////
////class Buyer implements Observer {
////    private final Map<Product, Integer> needs = new HashMap<>();
////    private double budget;
////    private final double incomePerTurn;
////    private final List<Seller> observedSellers = new ArrayList<>();
////    private final List<String> purchaseHistory = new ArrayList<>();
////    private final boolean detailedLogging;
////    private final int id;
////    private static int nextId = 1;
////    private final double priceSensitivity = 0.5;
////
////    public Buyer(double initialBudget, double incomePerTurn, boolean detailedLogging) {
////        this.budget = initialBudget;
////        this.incomePerTurn = incomePerTurn;
////        this.detailedLogging = detailedLogging;
////        this.id = nextId++;
////    }
////
////    public void addNeed(Product product, int priority) {
////        needs.put(product, priority);
////            System.out.println("Kupujący " + id + " dodał potrzebę: " + product +
////                    " (priorytet: " + priority + ")");
////
////    }
////
////    public void addObserver(Seller seller) {
////        observedSellers.add(seller);
////    }
////
////    public void makePurchaseDecisions(List<Seller> allSellers) {
////        budget += incomePerTurn;
////            System.out.println("Kupujący " + id + " rozpoczyna turę z budżetem: " + budget);
////
////
////        // Najpierw wymuszone zakupy produktów pierwszej potrzeby
////        purchaseNecessities(allSellers);
////
////        // Następnie zakupy produktów luksusowych (jeśli zostaną środki)
////        purchaseLuxuries(allSellers);
////    }
////
////    private void purchaseNecessities(List<Seller> allSellers) {
////        needs.entrySet().stream()
////                .filter(entry -> entry.getKey().getType() == Product.Type.NECESSITY)
////                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
////                .forEach(entry -> {
////                    Product product = entry.getKey();
////                    int requiredMinimum = entry.getValue(); // Minimalna liczba sztuk do przeżycia
////                    Optional<ProductOffer> bestOffer = findBestOffer(product, allSellers);
////
////                    if (bestOffer.isPresent()) {
////                        ProductOffer offer = bestOffer.get();
////                        // Kupuje minimum, ale jeśli cena jest wysoka, nie kupuje "na zapas"
////                        int desiredQuantity = requiredMinimum + (int) (1 / (1 + priceSensitivity * offer.getPrice()));
////                        int quantity = Math.min(desiredQuantity, (int) (budget / offer.getPrice()));
////                        quantity = Math.min(quantity, offer.getAvailableQuantity());
////
////                        if (quantity > 0) {
////                            executePurchase(product, quantity, offer, allSellers);
////                        }
////                    }
////                });
////    }
////
////    private void purchaseLuxuries(List<Seller> allSellers) {
////        needs.entrySet().stream()
////                .filter(entry -> entry.getKey().getType() == Product.Type.LUXURY)
////                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
////                .forEach(entry -> {
////                    Product product = entry.getKey();
////                    Optional<ProductOffer> bestOffer = findBestOffer(product, allSellers);
////
////                    bestOffer.ifPresent(offer -> {
////                        double maxAffordable = budget / offer.getPrice();
////                        // Im wyższa cena, tym mniejsza chęć zakupu (np. kupuje tylko 1 szt. zamiast 3)
////                        int desiredQuantity = (int) (entry.getValue() / (1 + priceSensitivity * offer.getPrice()));
////                        int quantity = Math.min(desiredQuantity, (int) maxAffordable);
////                        quantity = Math.min(quantity, offer.getAvailableQuantity());
////
////                        if (quantity > 0) {
////                            executePurchase(product, quantity, offer, allSellers);
////                        }
////                    });
////                });
////    }
////
////    private Optional<ProductOffer> findBestOffer(Product product, List<Seller> sellers) {
////        return sellers.stream()
////                .flatMap(seller -> seller.getOffers().stream())
////                .filter(offer -> offer.getProduct().equals(product) &&
////                        offer.getAvailableQuantity() > 0)
////                .min(Comparator.comparingDouble(ProductOffer::getPrice));
////    }
////
////    private void executePurchase(Product product, int quantity, ProductOffer offer, List<Seller> sellers) {
////        sellers.stream()
////                .filter(s -> s.getOffers().contains(offer))
////                .findFirst()
////                .ifPresent(seller -> {
////                    if (seller.sellProduct(product, quantity, this)) {
////                        double cost = quantity * offer.getPrice();
////                        budget -= cost;
////                        String log = "Kupujący " + id + " kupił " + quantity + "x " +
////                                product.getName() + " za " + String.format("%.2f", cost) +
////                                " (pozostały budżet: " + String.format("%.2f", budget) + ")";
////                        purchaseHistory.add(log);
////                            System.out.println(log);
////
////                    }
////                });
////    }
////
////    public double getBudget() {
////        return budget;
////    }
////
////    public Map<Product, Integer> getNeeds() {
////        return needs;
////    }
////
////    public List<String> getPurchaseHistory() {
////        return Collections.unmodifiableList(purchaseHistory);
////    }
////
////    @Override
////    public void update() {
////            System.out.println("Kupujący " + id + " otrzymał powiadomienie o zmianie cen");
////
////    }
////
////    @Override
////    public String toString() {
////        return "Kupujący " + id + " (budżet: " + String.format("%.2f", budget) + ")";
////    }
////}
//
////sumuje
////class CentralBank implements Observable {
////    private double inflation;
////    private double targetTaxRevenue;
////    private final List<Observer> observers = new ArrayList<>();
////    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
////    private static final int STABILITY_WINDOW = 5;
////    private final boolean detailedLogging;
////    private final List<String> inflationHistory = new ArrayList<>();
////    private double collectedTaxRevenue = 0;
////
////    public CentralBank(double initialInflation, boolean detailedLogging) {
////        this.inflation = initialInflation;
////        this.targetTaxRevenue = 1000;
////        this.detailedLogging = detailedLogging;
////        inflationHistory.add("Początkowa inflacja: " + String.format("%.2f", initialInflation * 100) + "%");
////    }
////
////    public void collectInflationTax(List<Seller> sellers) {
////        double taxFromSellers = sellers.stream()
////                .mapToDouble(seller -> {
////                    double tax = seller.getTotalProfit() * inflation;
////                    seller.payInflationTax(tax);
////                    if (detailedLogging) {
////                        System.out.println("Bank pobrał podatek " + String.format("%.2f", tax) +
////                                " od " + seller);
////                    }
////                    return tax;
////                })
////                .sum();
////
////        collectedTaxRevenue += taxFromSellers;
////
////        if (detailedLogging) {
////            System.out.println("Łączne wpływy podatkowe: " + String.format("%.2f", collectedTaxRevenue));
////        }
////    }
////
////    public void calculateInflation(List<Seller> sellers, List<Buyer> buyers) {
////        double totalTurnover = calculateMarketTurnover(sellers);
////        System.out.println("INFW"+String.format("%.2f", inflation));
////        double currentTaxRevenue = totalTurnover * inflation;
////        System.out.println("INFW2"+String.format("%.2f", currentTaxRevenue));
////        historicalTaxRevenues.add(currentTaxRevenue);
////
////        if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
////            historicalTaxRevenues.remove();
////        }
////
////        adjustInflation(currentTaxRevenue);
////        logInflationAdjustment(currentTaxRevenue, totalTurnover);
////        notifyObservers();
////    }
////
////    private double calculateMarketTurnover(List<Seller> sellers) {
////        return sellers.stream()
////                .flatMap(s -> s.getOffers().stream())
////                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
////                .sum();
////    }
////
////    private void adjustInflation(double currentTaxRevenue) {
////        double oldInflation = inflation;
////        if (currentTaxRevenue < targetTaxRevenue * 0.95) {
////            inflation *= 1.05;
////        } else if (currentTaxRevenue > targetTaxRevenue * 1.05) {
////            inflation *= 0.95;
////        }
////
////        if (inflation != oldInflation && detailedLogging) {
////            System.out.println("Dostosowano inflację z " +
////                    String.format("%.2f", oldInflation * 100) + "% na " +
////                    String.format("%.2f", inflation * 100) + "%");
////        }
////    }
////
////    private void logInflationAdjustment(double currentTaxRevenue, double totalTurnover) {
////        if (detailedLogging) {
////            System.out.println("\nANALIZA INFLACYJNA:");
////            System.out.println("- Obrót rynkowy: " + String.format("%.2f", totalTurnover));
////            System.out.println("- Wpływy podatkowe: " +
////                    String.format("%.2f", currentTaxRevenue) +
////                    " (docelowo: " + String.format("%.2f", targetTaxRevenue) + ")");
////            System.out.println("- Aktualna inflacja: " +
////                    String.format("%.2f", inflation * 100) + "%");
////        }
////
////        inflationHistory.add("Tura " + (inflationHistory.size()) + ": " +
////                String.format("%.2f", inflation * 100) + "% (wpływy: " +
////                String.format("%.2f", currentTaxRevenue) + ")");
////    }
////
////    public boolean checkStability() {
////        if (historicalTaxRevenues.size() < STABILITY_WINDOW) {
////            return false;
////        }
////
////        double avgRevenue = historicalTaxRevenues.stream()
////                .mapToDouble(Double::doubleValue)
////                .average()
////                .orElse(0);
////
////        double deviation = Math.abs(avgRevenue - targetTaxRevenue) / targetTaxRevenue;
////
////        if (detailedLogging) {
////            System.out.println("Sprawdzanie stabilności:");
////            System.out.println("- Średnia wpływów: " + String.format("%.2f", avgRevenue));
////            System.out.println("- Odchylenie: " + String.format("%.2f", deviation * 100) + "%");
////        }
////
////        return deviation < 0.05;
////    }
////
////    public double getInflation() {
////        return inflation;
////    }
////
////    public double getTargetTaxRevenue() {
////        return targetTaxRevenue;
////    }
////
////    public void setTargetTaxRevenue(double targetTaxRevenue) {
////        this.targetTaxRevenue = targetTaxRevenue;
////        if (detailedLogging) {
////            System.out.println("Ustawiono docelowe wpływy podatkowe na: " + targetTaxRevenue);
////        }
////    }
////
////    public List<String> getInflationHistory() {
////        return Collections.unmodifiableList(inflationHistory);
////    }
////
////    @Override
////    public void addObserver(Observer observer) {
////        observers.add(observer);
////    }
////
////    @Override
////    public void removeObserver(Observer observer) {
////        observers.remove(observer);
////    }
////
////    @Override
////    public void notifyObservers() {
////        observers.forEach(Observer::update);
////    }
////}
//
//
//
//class CentralBank implements Observable {
//    private double inflation;
//    private double targetTaxRevenue;
//    private final List<Observer> observers = new ArrayList<>();
//    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
//    private static final int STABILITY_WINDOW = 5;
//    private final boolean detailedLogging;
//    private final List<String> inflationHistory = new ArrayList<>();
//    private double collectedTaxRevenue = 0;
//
//    public CentralBank(double initialInflation, boolean detailedLogging) {
//        this.inflation = initialInflation;
//        this.targetTaxRevenue = 1000;
//        this.detailedLogging = detailedLogging;
//        inflationHistory.add("Początkowa inflacja: " + String.format("%.2f", initialInflation * 100) + "%");
//    }
//
//    public void collectInflationTax(List<Seller> sellers) {
//        double taxFromSellers = sellers.stream()
//                .mapToDouble(seller -> {
//                    double tax = seller.getTotalProfit() * inflation;
//                    seller.payInflationTax(tax);
//                        System.out.println("Bank pobrał podatek " + String.format("%.2f", tax) +
//                                " od " + seller);
//
//                    return tax;
//                })
//                .sum();
//
//        collectedTaxRevenue += taxFromSellers;
//
//            System.out.println("Łączne wpływy podatkowe: " + String.format("%.2f", collectedTaxRevenue));
//
//    }
//
//    public void calculateInflation(List<Seller> sellers, List<Buyer> buyers) {
//        double totalTurnover = calculateMarketTurnover(sellers);
//        System.out.println("INFW"+String.format("%.2f", inflation));
//        double currentTaxRevenue = totalTurnover * inflation;
//        System.out.println("INFW2"+String.format("%.2f", currentTaxRevenue));
//        historicalTaxRevenues.add(currentTaxRevenue);
//
//        if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
//            historicalTaxRevenues.remove();
//        }
//
//        adjustInflation(currentTaxRevenue);
//        logInflationAdjustment(currentTaxRevenue, totalTurnover);
//        notifyObservers();
//    }
//
//    private double calculateMarketTurnover(List<Seller> sellers) {
//        return sellers.stream()
//                .flatMap(s -> s.getOffers().stream())
//                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
//                .sum();
//    }
//
//    private void adjustInflation(double currentTaxRevenue) {
//        System.out.println("curtas"+ String.format("%.2f", currentTaxRevenue));
//        double oldInflation = inflation;
//        if (currentTaxRevenue < targetTaxRevenue * 0.90) {
//            inflation *= 1.25;
//        } else if (currentTaxRevenue > targetTaxRevenue * 1.05) {
//            inflation *= 0.85;
//        }
//
//        if (inflation != oldInflation) {
//            System.out.println("Dostosowano inflację z " +
//                    String.format("%.2f", oldInflation * 100) + "% na " +
//                    String.format("%.2f", inflation * 100) + "%");
//        }
//    }
//
//    private void logInflationAdjustment(double currentTaxRevenue, double totalTurnover) {
//            System.out.println("\nANALIZA INFLACYJNA:");
//            System.out.println("- Obrót rynkowy: " + String.format("%.2f", totalTurnover));
//            System.out.println("- Wpływy podatkowe: " +
//                    String.format("%.2f", currentTaxRevenue) +
//                    " (docelowo: " + String.format("%.2f", targetTaxRevenue) + ")");
//            System.out.println("- Aktualna inflacja: " +
//                    String.format("%.2f", inflation * 100) + "%");
//
//
//        inflationHistory.add("Tura " + (inflationHistory.size()) + ": " +
//                String.format("%.2f", inflation * 100) + "% (wpływy: " +
//                String.format("%.2f", currentTaxRevenue) + ")");
//    }
//
//    public boolean checkStability() {
//        if (historicalTaxRevenues.size() < STABILITY_WINDOW) {
//            return false;
//        }
//
//        double avgRevenue = historicalTaxRevenues.stream()
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElse(0);
//
//        double deviation = Math.abs(avgRevenue - targetTaxRevenue) / targetTaxRevenue;
//
//            System.out.println("Sprawdzanie stabilności:");
//            System.out.println("- Średnia wpływów: " + String.format("%.2f", avgRevenue));
//            System.out.println("- Odchylenie: " + String.format("%.2f", deviation * 100) + "%");
//
//
//        return deviation < 0.05;
//    }
//
//    public int getObserversCount() {
//        return observers.size();
//    }
//
//    public Queue<Double> getHistoricalTaxRevenues() {
//        return historicalTaxRevenues;
//    }
//
//    public double getInflation() {
//        return inflation;
//    }
//
//    public double getTargetTaxRevenue() {
//        return targetTaxRevenue;
//    }
//
//    public void setTargetTaxRevenue(double targetTaxRevenue) {
//        this.targetTaxRevenue = targetTaxRevenue;
//            System.out.println("Ustawiono docelowe wpływy podatkowe na: " + targetTaxRevenue);
//
//    }
//
//    public List<String> getInflationHistory() {
//        return Collections.unmodifiableList(inflationHistory);
//    }
//
//    @Override
//    public void addObserver(Observer observer) {
//        observers.add(observer);
//    }
//
//    @Override
//    public void removeObserver(Observer observer) {
//        observers.remove(observer);
//    }
//
//    @Override
//    public void notifyObservers() {
//        observers.forEach(Observer::update);
//    }
//}
//
//interface Visitor {
//    void visit(Seller seller);
//}
//
//class PriceUpdateVisitor implements Visitor {
//    private final double inflation;
//    private final boolean detailedLogging;
//
//    public PriceUpdateVisitor(double inflation, boolean detailedLogging) {
//        this.inflation = inflation;
//        this.detailedLogging = detailedLogging;
//    }
//
//    @Override
//    public void visit(Seller seller) {
//            System.out.println("Aktualizacja cen dla " + seller);
//
//        seller.adjustPrices(inflation);
//    }
//}
