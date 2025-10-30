////package org.example;
////
////import java.util.*;
////
////class CentralBank implements Observable, Observer {
////    private double inflation;
////    private double targetTaxRevenue;
////    private final List<Observer> observers = new ArrayList<>();
////    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
////    private static final int STABILITY_WINDOW = 5;
////    private final List<String> inflationHistory = new ArrayList<>();
////    private double collectedTaxRevenue = 0;
////    private boolean isStable = false;
////
////
////    public CentralBank(double initialInflation, boolean detailedLogging) {
////        this.inflation = initialInflation;
////        this.targetTaxRevenue = 1000;
////        inflationHistory.add("Tura 0: " + String.format("%.2f", initialInflation * 100) + "% (wpływy: 0)");
////    }
////
////    public void collectInflationTax(List<Seller> sellers) {
////        double taxFromSellers = sellers.stream()
////                .mapToDouble(seller -> {
////                    double tax = seller.getTotalProfit() * inflation;
////                    seller.payInflationTax(tax);
////                        System.out.println("Bank pobrał podatek " + String.format("%.2f", tax) +
////                                " od " + seller);
////
////                    return tax;
////                })
////                .sum();
////
////        collectedTaxRevenue += taxFromSellers;
////
////            System.out.println("Łączne wpływy podatkowe: " + String.format("%.2f", collectedTaxRevenue));
////
////    }
////
////    public void calculateInflation(List<Seller> sellers, List<Buyer> buyers) {
////        double totalTurnover = calculateMarketTurnover(sellers);
////        double currentTaxRevenue = totalTurnover * inflation;
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
////    public double calculateMarketTurnover(List<Seller> sellers) {
////        return sellers.stream()
////                .flatMap(s -> s.getOffers().stream())
////                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
////                .sum();
////    }
////
////    public void adjustInflation(double currentTaxRevenue) {
////        System.out.println("curtas"+ String.format("%.2f", currentTaxRevenue));
////        double oldInflation = inflation;
////        if (currentTaxRevenue < targetTaxRevenue * 0.90) {
////            inflation *= 1.25;
////        } else if (currentTaxRevenue > targetTaxRevenue * 1.05) {
////            inflation *= 0.85;
////        }
////
////        if (inflation != oldInflation) {
////            System.out.println("Dostosowano inflację z " +
////                    String.format("%.2f", oldInflation * 100) + "% na " +
////                    String.format("%.2f", inflation * 100) + "%");
////        }
////    }
////
////    private void logInflationAdjustment(double currentTaxRevenue, double totalTurnover) {
////            System.out.println("\nANALIZA INFLACYJNA:");
////            System.out.println("- Obrót rynkowy: " + String.format("%.2f", totalTurnover));
////            System.out.println("- Wpływy podatkowe: " +
////                    String.format("%.2f", currentTaxRevenue) +
////                    " (docelowo: " + String.format("%.2f", targetTaxRevenue) + ")");
////            System.out.println("- Aktualna inflacja: " +
////                    String.format("%.2f", inflation * 100) + "%");
////
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
////            System.out.println("Sprawdzanie stabilności:");
////            System.out.println("- Średnia wpływów: " + String.format("%.2f", avgRevenue));
////            System.out.println("- Odchylenie: " + String.format("%.2f", deviation * 100) + "%");
////
////
//////        return deviation < 0.05;
////
////        boolean nowStable = deviation < 0.05;
////        if (nowStable) {
////            isStable = true;
////        }
////        return nowStable;
////    }
////
////    public int getObserversCount() {
////        return observers.size();
////    }
////
////    public Queue<Double> getHistoricalTaxRevenues() {
////        return historicalTaxRevenues;
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
////            System.out.println("Ustawiono docelowe wpływy podatkowe na: " + targetTaxRevenue);
////
////    }
////
////    public void setInflation(double inflation) {
////        this.inflation = inflation;
////
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
//////        observers.forEach(Observer::update(inflation));
////        observers.forEach(o -> o.update(inflation));
////    }
////
////    public double getCurrentRevenue(List<Seller> sellers) {
////        return inflation * calculateMarketTurnover(sellers);
////    }
////
////
////    public List<Double> getInflationPercentages() {
////        List<Double> percentages = new ArrayList<>();
////        for (String entry : inflationHistory) {
////            try {
////                int colonIndex = entry.indexOf(":");
////                int percentIndex = entry.indexOf("%");
////                if (colonIndex >= 0 && percentIndex > colonIndex) {
////                    // Wytnij fragment, np. " 5,00"
////                    String percentStr = entry.substring(colonIndex + 1, percentIndex).trim();
////                    // Zamień przecinek na kropkę, żeby parsować na double
////                    percentStr = percentStr.replace(',', '.');
////                    double value = Double.parseDouble(percentStr);
////                    percentages.add(value);
////                }
////            } catch (Exception e) {
////                // Pomiń błędy parsowania (np. dla wpisów innych niż tury)
////            }
////        }
////        return percentages;
////    }
////
////    @Override
////    public void update(ProductOffer offer) {
////        // Nie reaguj na zmianę ceny
////    }
////
////    @Override
////    public void updateEconomicData(double value) {
////        // Dostosuj inflację na podstawie zysków/budżetów
////    }
////
////    @Override
////    public void update(double offer){
////
////    }
////
////    @Override
////    public void onPurchase(Buyer buyer, Product product, int quantity, double pricePerUnit) {
////        double totalSpent = quantity * pricePerUnit;
////
////        // Zarejestruj zakup
////        System.out.println("BANK: Kupujący " + buyer.getName() +
////                " kupił " + quantity + " x " + product.getName() + " za " + pricePerUnit + " szt.");
////
////        // Logika banku – np. zwiększ inflację jeśli łączne wydatki rosną
////        adjustInflationBasedOnSpending(totalSpent);
////    }
////
////    private void adjustInflationBasedOnSpending(double spending) {
////        // Przykład: prosta zależność
////        if (spending > 1000) {
////            increaseInflation(0.01); // +1% inflacji
////        }
////    }
////
////    private void increaseInflation(double delta) {
////        // Zmienna inflacji wewnątrz banku
////        System.out.println("BANK: Inflacja wzrasta o " + (delta * 100) + "%");
////    }
////}
////
////
////
//
////
////package org.example;
////
////import java.util.*;
////
////public class CentralBank implements Observable, Observer {
////    private double inflation;
////    private double targetTaxRevenue;
////    private final List<Observer> observers = new ArrayList<>();
////    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
////    private static final int STABILITY_WINDOW = 5;
////    private final List<String> inflationHistory = new ArrayList<>();
////    private double collectedTaxRevenue = 0;
////    private boolean isStable = false;
////    private double totalCollectedTax = 0;
////
////
////    public CentralBank(double initialInflation) {
////        this.inflation = initialInflation;
////        this.targetTaxRevenue = 100;
////        inflationHistory.add("Tura 0: " + formatPercent(initialInflation) + " (wpływy: 0)");
////    }
////
////    // 1. PODATEK OD SPRZEDAWCÓW
////    public void collectInflationTax(List<Seller> sellers) {
////        double taxFromSellers = sellers.stream()
////                .mapToDouble(seller -> {
////                    double tax = seller.getTotalProfit() * inflation;
////                    seller.payInflationTax(tax);
////                    System.out.println("Bank pobrał podatek " + format(tax) + " od " + seller);
////                    return tax;
////                })
////                .sum();
////        collectedTaxRevenue += taxFromSellers;
////        System.out.println("Łączne wpływy podatkowe: " + format(collectedTaxRevenue));
////    }
////
////    // 2. OBLICZ INFLACJĘ
//////    public void calculateInflation(List<Seller> sellers) {
//////        double totalTurnover = calculateMarketTurnover(sellers);
////////        double currentTaxRevenue = totalTurnover * inflation;
//////        double currentTaxRevenue = collectedTaxRevenue;
//////
//////        historicalTaxRevenues.add(currentTaxRevenue);
//////
//////        if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
//////            historicalTaxRevenues.remove();
//////        }
//////
//////        adjustInflation(currentTaxRevenue);
//////        logInflationAdjustment(currentTaxRevenue, totalTurnover);
//////        notifyObservers();
//////        collectedTaxRevenue = 0;
//////
//////    }
////
//////public void calculateInflation(List<Seller> sellers) {
//////    double totalTurnover = calculateMarketTurnover(sellers);
//////    // Ustaw target jako procent obrotu rynkowego (np. 5%)
//////    this.targetTaxRevenue = totalTurnover * 0.05;
//////
//////    double currentTaxRevenue = collectedTaxRevenue;
//////    historicalTaxRevenues.add(currentTaxRevenue);
//////
//////    if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
//////        historicalTaxRevenues.remove();
//////    }
//////
//////    adjustInflation(currentTaxRevenue);
//////    logInflationAdjustment(currentTaxRevenue, totalTurnover);
//////    notifyObservers();
//////    collectedTaxRevenue = 0;
//////}
////
////public void calculateInflation(List<Seller> sellers) {
////    double totalTurnover = calculateMarketTurnover(sellers);
////
////    // Użyj całkowitego zebranego podatku jako currentTaxRevenue
////    double currentTaxRevenue = totalCollectedTax;
////    historicalTaxRevenues.add(currentTaxRevenue);
////
////    if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
////        historicalTaxRevenues.remove();
////    }
////
////    // Oblicz docelowy podatek jako procent obrotu
////    this.targetTaxRevenue = totalTurnover * 0.05; // 5% obrotu
////
////    adjustInflation(currentTaxRevenue);
////    logInflationAdjustment(currentTaxRevenue, totalTurnover);
////    notifyObservers();
////    totalCollectedTax = 0; // Wyzeruj po obliczeniach
////}
////
////    // Metoda do rejestrowania płatności podatku
////    public void registerTaxPayment(double amount) {
////        totalCollectedTax += amount;
////    }
////
////    public double calculateMarketTurnover(List<Seller> sellers) {
////        return sellers.stream()
////                .flatMap(s -> s.getOffers().stream())
////                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
////                .sum();
////    }
////
////    public void adjustInflation(double currentTaxRevenue) {
////        double oldInflation = inflation;
////        if (currentTaxRevenue < targetTaxRevenue * 0.90) {
////            inflation *= 1.25;
////        } else if (currentTaxRevenue > targetTaxRevenue * 1.05) {
////            inflation *= 0.85;
////        }
////
////        if (inflation != oldInflation) {
////            System.out.println("Dostosowano inflację z " + formatPercent(oldInflation) +
////                    " na " + formatPercent(inflation));
////        }
////    }
////
////    private void logInflationAdjustment(double currentTaxRevenue, double totalTurnover) {
////        System.out.println("\nANALIZA INFLACYJNA:");
////        System.out.println("- Obrót rynkowy: " + format(totalTurnover));
////        System.out.println("- Wpływy podatkowe: " + format(currentTaxRevenue) +
////                " (docelowo: " + format(targetTaxRevenue) + ")");
////        System.out.println("- Aktualna inflacja: " + formatPercent(inflation));
////
////        inflationHistory.add("Tura " + inflationHistory.size() + ": " +
////                formatPercent(inflation) + " (wpływy: " + format(currentTaxRevenue) + ")");
////    }
////
////    // 3. STABILNOŚĆ SYSTEMU
////    public boolean checkStability() {
////        if (historicalTaxRevenues.size() < STABILITY_WINDOW) return false;
////
////        double avgRevenue = historicalTaxRevenues.stream()
////                .mapToDouble(Double::doubleValue)
////                .average().orElse(0);
////        double deviation = Math.abs(avgRevenue - targetTaxRevenue) / targetTaxRevenue;
////
////        System.out.println("Sprawdzanie stabilności:");
////        System.out.println("- Średnia wpływów: " + format(avgRevenue));
////        System.out.println("- Odchylenie: " + format(deviation * 100) + "%");
////
////        boolean nowStable = deviation < 0.05;
////        if (nowStable) isStable = true;
////
////        return nowStable;
////    }
////
////    // 4. OBSERWATOR
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
////        observers.forEach(o -> o.update(inflation));
////    }
////
////    // 5. OBSERWACJA ZAKUPÓW KUPUJĄCYCH
////    @Override
////    public void onPurchase(Buyer buyer, Product product, int quantity, double pricePerUnit) {
////        double totalSpent = quantity * pricePerUnit;
////        System.out.println("BANK: Kupujący " +
////                " kupił " + quantity + " x " + product.getName() + " za " + format(pricePerUnit));
////
////        adjustInflationBasedOnSpending(totalSpent);
////    }
////
////    private void adjustInflationBasedOnSpending(double spending) {
////        if (spending > 1000) {
////            increaseInflation(0.01); // +1%
////        }
////    }
////
////    private void increaseInflation(double delta) {
////        inflation += delta;
////        System.out.println("BANK: Inflacja wzrasta o " + format(delta * 100) + "% (nowa: " + formatPercent(inflation) + ")");
////    }
////
////    // 6. GETTERY I SETTERY
////    public double getInflation() {
////        return inflation;
////    }
////
////    public void setInflation(double inflation) {
////        this.inflation = inflation;
////    }
////
////    public void setTargetTaxRevenue(double targetTaxRevenue) {
////        this.targetTaxRevenue = targetTaxRevenue;
////        System.out.println("Ustawiono docelowe wpływy podatkowe na: " + format(targetTaxRevenue));
////    }
////
////    public double getTargetTaxRevenue() {
////        return targetTaxRevenue;
////    }
////
////    public List<String> getInflationHistory() {
////        return Collections.unmodifiableList(inflationHistory);
////    }
////
////    public List<Double> getInflationPercentages() {
////        List<Double> percentages = new ArrayList<>();
////        for (String entry : inflationHistory) {
////            try {
////                int colonIndex = entry.indexOf(":");
////                int percentIndex = entry.indexOf("%");
////                if (colonIndex >= 0 && percentIndex > colonIndex) {
////                    String percentStr = entry.substring(colonIndex + 1, percentIndex).trim().replace(',', '.');
////                    double value = Double.parseDouble(percentStr);
////                    percentages.add(value);
////                }
////            } catch (Exception ignored) {
////            }
////        }
////        return percentages;
////    }
////
////    // 7. FORMAT POMOCNICZY
////    private String format(double val) {
////        return String.format("%.2f", val);
////    }
////
////    private String formatPercent(double val) {
////        return String.format("%.2f", val * 100) + "%";
////    }
////
////    public void update(ProductOffer a){
////
////    }
////
////    public void update(double a){
////
////    }
////
////    public double getCurrentInflation(){
////        return inflation;
////    }
////
////    @Override
////    public void updateBudget(Buyer buyer, double budget) {
////        System.out.println("BANK: Kupujący " + buyer + " zgłosił budżet: " + format(budget));
////
////        if (budget < 200) {
////            reduceInflation(0.01);  // zmniejsz inflację o 1%
////        } else if (budget > 1000) {
////            increaseInflation(0.01);  // zwiększ inflację o 1%
////        }
////    }
////    private void reduceInflation(double delta) {
////        inflation = Math.max(0.0, inflation - delta);
////        System.out.println("BANK: Inflacja maleje o " + format(delta * 100) + "% (nowa: " + formatPercent(inflation) + ")");
////    }
////
////
////
////}
////
////
//
//
//
//
//
//
//
//
//
//
//
//
//
//// CentralBank.java
//package org.example;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class CentralBank implements Observable, Observer {
//    private double inflation;
//    private double targetTaxRevenue;
//    private final List<Observer> observers = new ArrayList<>();
//    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
//    private static final int STABILITY_WINDOW = 5;
//    private final List<String> inflationHistory = new ArrayList<>();
//    private double currentTaxRevenue = 0;
//    private boolean isStable = false;
//    private double taxRate = 0.05; // Stała stawka podatku
//
//    public CentralBank(double initialInflation) {
//        this.inflation = Math.max(0.01, Math.min(0.5, initialInflation));
//        this.targetTaxRevenue = 100;
//        inflationHistory.add("Tura 0: " + formatPercent(initialInflation) + " (wpływy: 0)");
//    }
//
////    public void collectTaxes(List<Seller> sellers) {
////        currentTaxRevenue = sellers.stream()
////                .mapToDouble(seller -> {
////                    double tax = seller.getTotalProfit() * taxRate;
////                    seller.payInflationTax(tax);
////                    return tax;
////                })
////                .sum();
////
////        System.out.println("Bank zebrał podatki: " + format(currentTaxRevenue));
////    }
//
//    public void calculateInflation(List<Seller> sellers) {
//        double totalTurnover = calculateMarketTurnover(sellers);
//        historicalTaxRevenues.add(currentTaxRevenue);
//
//        if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
//            historicalTaxRevenues.remove();
//        }
//
//        adjustInflation();
//        logInflationAdjustment(totalTurnover);
//        notifyObservers();
//        currentTaxRevenue = 0;
//    }
//
//    private double calculateMarketTurnover(List<Seller> sellers) {
//        return sellers.stream()
//                .flatMap(s -> s.getOffers().stream())
//                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
//                .sum();
//    }
//
//    private void adjustInflation() {
//        double oldInflation = inflation;
//
//        if (historicalTaxRevenues.size() < STABILITY_WINDOW) {
//            return;
//        }
//
//        double avgRevenue = historicalTaxRevenues.stream()
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElse(targetTaxRevenue);
//
//        double ratio = avgRevenue / targetTaxRevenue;
//
//        if (ratio < 0.95) {
//            inflation = Math.min(0.5, inflation * 1.05);
//        } else if (ratio > 1.05) {
//            inflation = Math.max(0.01, inflation * 0.95);
//        }
//
//        if (inflation != oldInflation) {
//            System.out.println("Dostosowano inflację z " + formatPercent(oldInflation) +
//                    " na " + formatPercent(inflation));
//        }
//    }
//
//    private void logInflationAdjustment(double totalTurnover) {
//        System.out.println("\nANALIZA INFLACYJNA:");
//        System.out.println("- Obrót rynkowy: " + format(totalTurnover));
//        System.out.println("- Wpływy podatkowe: " + format(currentTaxRevenue) +
//                " (docelowo: " + format(targetTaxRevenue) + ")");
//        System.out.println("- Aktualna inflacja: " + formatPercent(inflation));
//
//        inflationHistory.add("Tura " + inflationHistory.size() + ": " +
//                formatPercent(inflation) + " (wpływy: " +
//                format(currentTaxRevenue) + ")");
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
//        System.out.println("Sprawdzanie stabilności:");
//        System.out.println("wplyw"+ currentTaxRevenue);
//        System.out.println("- Średnia wpływów: " + format(avgRevenue));
//        System.out.println("- Odchylenie: " + format(deviation * 100) + "%");
//
//        boolean nowStable = deviation < 0.05;
//        if (nowStable) isStable = true;
//
//        return nowStable;
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
//        observers.forEach(o -> o.update(inflation));
//    }
//
//    @Override
//    public void update(ProductOffer offer) {}
//
//    @Override
//    public void update(double amount) {
//        System.out.println("TUTAJ");
//        currentTaxRevenue += amount;
//        System.out.println("TUTAJ"+ currentTaxRevenue);
//    }
//
//    @Override
//    public void onPurchase(Buyer buyer, Product product, int quantity, double pricePerUnit) {
//        double totalSpent = quantity * pricePerUnit;
//        if (totalSpent > 1000) {
//            increaseInflation(0.005);
//        }
//    }
//
//    private void increaseInflation(double delta) {
//        inflation = Math.min(0.5, inflation + delta);
//    }
//
//    // Helper methods
//    private String format(double val) {
//        return String.format("%.2f", val);
//    }
//
//    private String formatPercent(double val) {
//        return String.format("%.2f", val * 100) + "%";
//    }
//
//    // Getters
//    public double getInflation() { return inflation; }
//    public List<String> getInflationHistory() { return Collections.unmodifiableList(inflationHistory); }
//    public List<Double> getInflationPercentages() {
//        return inflationHistory.stream()
//                .map(entry -> {
//                    try {
//                        String percentStr = entry.split("%")[0].split(":")[1].trim();
//                        return Double.parseDouble(percentStr);
//                    } catch (Exception e) {
//                        return 0.0;
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//    public void updateBudget(Buyer b, double d){
//
//    }
//}









//
//package org.example;
//
//import java.util.*;
//
//class CentralBank implements Observable {
//    private double inflation;
//    private double targetTaxRevenue;
//    private final List<Observer> observers = new ArrayList<>();
//    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
//    private static final int STABILITY_WINDOW = 5;
//    private final List<String> inflationHistory = new ArrayList<>();
//    private double collectedTaxRevenue = 0;
//    private boolean isStable = false;
//
//
//    public CentralBank(double initialInflation, boolean detailedLogging) {
//        this.inflation = initialInflation;
//        this.targetTaxRevenue = 1000;
//        inflationHistory.add("Tura 0: " + String.format("%.2f", initialInflation * 100) + "% (wpływy: 0)");
//    }
//
//    public void collectInflationTax(List<Seller> sellers) {
//        double taxFromSellers = sellers.stream()
//                .mapToDouble(seller -> {
//                    double tax = seller.getTotalProfit() * inflation;
//                    seller.payInflationTax(tax);
//                    System.out.println("Bank pobrał podatek " + String.format("%.2f", tax) +
//                            " od " + seller);
//
//                    return tax;
//                })
//                .sum();
//
//        collectedTaxRevenue += taxFromSellers;
//
//        System.out.println("Łączne wpływy podatkowe: " + String.format("%.2f", collectedTaxRevenue));
//
//    }
//
//    public void calculateInflation(List<Seller> sellers, List<Buyer> buyers) {
//        double totalTurnover = calculateMarketTurnover(sellers);
//        double currentTaxRevenue = totalTurnover * inflation;
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
//    public double calculateMarketTurnover(List<Seller> sellers) {
//        return sellers.stream()
//                .flatMap(s -> s.getOffers().stream())
//                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
//                .sum();
//    }
//
//    public void adjustInflation(double currentTaxRevenue) {
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
//        System.out.println("\nANALIZA INFLACYJNA:");
//        System.out.println("- Obrót rynkowy: " + String.format("%.2f", totalTurnover));
//        System.out.println("- Wpływy podatkowe: " +
//                String.format("%.2f", currentTaxRevenue) +
//                " (docelowo: " + String.format("%.2f", targetTaxRevenue) + ")");
//        System.out.println("- Aktualna inflacja: " +
//                String.format("%.2f", inflation * 100) + "%");
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
//        System.out.println("Sprawdzanie stabilności:");
//        System.out.println("- Średnia wpływów: " + String.format("%.2f", avgRevenue));
//        System.out.println("- Odchylenie: " + String.format("%.2f", deviation * 100) + "%");
//
//
////        return deviation < 0.05;
//
//        boolean nowStable = deviation < 0.05;
//        if (nowStable) {
//            isStable = true;
//        }
//        return nowStable;
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
//        System.out.println("Ustawiono docelowe wpływy podatkowe na: " + targetTaxRevenue);
//
//    }
//
//    public void setInflation(double inflation) {
//        this.inflation = inflation;
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
////        observers.forEach(Observer::update(inflation));
//        observers.forEach(o -> o.update(inflation));
//    }
//
//    public double getCurrentRevenue(List<Seller> sellers) {
//        return inflation * calculateMarketTurnover(sellers);
//    }
//
//
//    public List<Double> getInflationPercentages() {
//        List<Double> percentages = new ArrayList<>();
//        for (String entry : inflationHistory) {
//            try {
//                int colonIndex = entry.indexOf(":");
//                int percentIndex = entry.indexOf("%");
//                if (colonIndex >= 0 && percentIndex > colonIndex) {
//                    // Wytnij fragment, np. " 5,00"
//                    String percentStr = entry.substring(colonIndex + 1, percentIndex).trim();
//                    // Zamień przecinek na kropkę, żeby parsować na double
//                    percentStr = percentStr.replace(',', '.');
//                    double value = Double.parseDouble(percentStr);
//                    percentages.add(value);
//                }
//            } catch (Exception e) {
//                // Pomiń błędy parsowania (np. dla wpisów innych niż tury)
//            }
//        }
//        return percentages;
//    }
//
//
//}
//






package org.example;

import java.util.*;

class CentralBank implements Observable, Observer {
    private double inflation;
    private double targetTaxRevenue;
    private final List<Observer> observers = new ArrayList<>();
    private final Queue<Double> historicalTaxRevenues = new LinkedList<>();
    private static final int STABILITY_WINDOW = 5;
    private final List<String> inflationHistory = new ArrayList<>();
    private double collectedTaxRevenue = 0;
    private boolean isStable = false;
    private double budgetBuyers = 0.0;


    public CentralBank(double initialInflation, boolean detailedLogging) {
        this.inflation = initialInflation;
        this.targetTaxRevenue = 1000;
        inflationHistory.add("Tura 0: " + String.format("%.2f", initialInflation * 100) + "% (wpływy: 0)");
    }

    public void collectInflationTax(List<Seller> sellers) {
        double taxFromSellers = sellers.stream()
                .mapToDouble(seller -> {
                    double tax = seller.getTotalProfit() * inflation;
                    seller.payInflationTax(tax);
                    System.out.println("Bank pobrał podatek " + String.format("%.2f", tax) +
                            " od " + seller);

                    return tax;
                })
                .sum();

        collectedTaxRevenue += taxFromSellers;

        System.out.println("Łączne wpływy podatkowe: " + String.format("%.2f", collectedTaxRevenue));
    }

    public void calculateInflation(List<Seller> sellers, List<Buyer> buyers) {
        double totalTurnover = calculateMarketTurnover(sellers);
        double currentTaxRevenue = totalTurnover * inflation;
        historicalTaxRevenues.add(currentTaxRevenue);

        if (historicalTaxRevenues.size() > STABILITY_WINDOW) {
            historicalTaxRevenues.remove();
        }

        adjustInflation(currentTaxRevenue);
        logInflationAdjustment(currentTaxRevenue, totalTurnover);
        notifyObservers();
    }

    public double calculateMarketTurnover(List<Seller> sellers) {
        return sellers.stream()
                .flatMap(s -> s.getOffers().stream())
                .mapToDouble(o -> o.getPrice() * o.getAvailableQuantity())
                .sum();
    }

    public void adjustInflation(double currentTaxRevenue) {
        double oldInflation = inflation;
        if (currentTaxRevenue < targetTaxRevenue * 0.90) {
            inflation *= 1.25;
        } else if (currentTaxRevenue > targetTaxRevenue * 1.05) {
            inflation *= 0.85;
        }

        if (inflation != oldInflation) {
            System.out.println("Dostosowano inflację z " +
                    String.format("%.2f", oldInflation * 100) + "% na " +
                    String.format("%.2f", inflation * 100) + "%");
        }
        notifyObservers();
    }

    private void logInflationAdjustment(double currentTaxRevenue, double totalTurnover) {
        System.out.println("\nANALIZA INFLACYJNA:");
        System.out.println("- Obrót rynkowy: " + String.format("%.2f", totalTurnover));
        System.out.println("- Wpływy podatkowe: " +
                String.format("%.2f", currentTaxRevenue) +
                " (docelowo: " + String.format("%.2f", targetTaxRevenue) + ")");
        System.out.println("- Aktualna inflacja: " +
                String.format("%.2f", inflation * 100) + "%");


        inflationHistory.add("Tura " + (inflationHistory.size()) + ": " +
                String.format("%.2f", inflation * 100) + "% (wpływy: " +
                String.format("%.2f", currentTaxRevenue) + ")");
    }

    public boolean checkStability() {
        if (historicalTaxRevenues.size() < STABILITY_WINDOW) {
            return false;
        }

        double avgRevenue = historicalTaxRevenues.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double deviation = Math.abs(avgRevenue - targetTaxRevenue) / targetTaxRevenue;

        System.out.println("Sprawdzanie stabilności:");
        System.out.println("- Średnia wpływów: " + String.format("%.2f", avgRevenue));
        System.out.println("- Odchylenie: " + String.format("%.2f", deviation * 100) + "%");


//        return deviation < 0.05;

        boolean nowStable = deviation < 0.05;
        if (nowStable) {
            isStable = true;
        }else{
            isStable = false;
        }
        return nowStable;
    }

    public int getObserversCount() {
        return observers.size();
    }

    public Queue<Double> getHistoricalTaxRevenues() {
        return historicalTaxRevenues;
    }

    public double getInflation() {
        return inflation;
    }

    public double getTargetTaxRevenue() {
        return targetTaxRevenue;
    }

    public void setTargetTaxRevenue(double targetTaxRevenue) {
        this.targetTaxRevenue = targetTaxRevenue;
        System.out.println("Ustawiono docelowe wpływy podatkowe na: " + targetTaxRevenue);

    }


    public void setInflation(double inflation) {
        this.inflation = inflation;

    }

    public List<String> getInflationHistory() {
        return Collections.unmodifiableList(inflationHistory);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
//        observers.forEach(Observer::update(inflation));
        observers.forEach(o -> o.update(inflation));
    }

    public double getCurrentRevenue(List<Seller> sellers) {
        return inflation * calculateMarketTurnover(sellers);
    }


    public List<Double> getInflationPercentages() {
        List<Double> percentages = new ArrayList<>();
        for (String entry : inflationHistory) {
            try {
                int colonIndex = entry.indexOf(":");
                int percentIndex = entry.indexOf("%");
                if (colonIndex >= 0 && percentIndex > colonIndex) {
                    // Wytnij fragment, np. " 5,00"
                    String percentStr = entry.substring(colonIndex + 1, percentIndex).trim();
                    // Zamień przecinek na kropkę, żeby parsować na double
                    percentStr = percentStr.replace(',', '.');
                    double value = Double.parseDouble(percentStr);
                    percentages.add(value);
                }
            } catch (Exception e) {
                // Pomiń błędy parsowania (np. dla wpisów innych niż tury)
            }
        }
        return percentages;
    }

    public void update(Observable o, double profit){
        if (o instanceof Seller){
            System.out.println("POWIAdomil " + profit);
            this.collectedTaxRevenue += profit;
            System.out.println("POWIAdomil2 " +
                    this.collectedTaxRevenue);
        }else{
            budgetBuyers = profit;
        }

    }

    public void update(double d){

    }


    public void update(ProductOffer p){

    }

}