
package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class Seller implements Observer, Observable{
    private final List<ProductOffer> offers = new ArrayList<>();
    private double profitMargin;
    private double totalProfit = 0;
    private final List<String> transactionLog = new ArrayList<>();
    private final boolean detailedLogging;
    private double currentInflation;
    private List<Observer> observers = new ArrayList<>();

    public double purpose  = 2000;


    public Seller(double initialProfitMargin, boolean detailedLogging) {
        this.profitMargin = initialProfitMargin;
        this.detailedLogging = detailedLogging;
    }

    public void addProductOffer(Product product, int initialQuantity) {
        double initialPrice = product.getProductionCost() * (1 + profitMargin);
        offers.add(new ProductOffer(product, initialPrice, initialQuantity));
        if (detailedLogging) {
            System.out.println("Dodano produkt: " + product.getName() +
                    ", cena: " + initialPrice +
                    ", ilość: " + initialQuantity);
        }
    }

    public List<ProductOffer> getOffers() {
        return Collections.unmodifiableList(offers);
    }

    public void adjustPrices() {
        double inflation = currentInflation;
        for (ProductOffer offer : offers) {
            double oldPrice = offer.getPrice();
            double basePrice = offer.getBasePrice();
            double newPrice = basePrice * (1 + inflation);
            offer.setPrice(newPrice);

            System.out.println("Dostosowanie ceny " + offer.getProduct().getName() +
                    ": " + String.format("%.2f", oldPrice) + " -> " +
                    String.format("%.2f", newPrice) +
                    " (inflacja: " + String.format("%.2f", inflation * 100) + "%)");

        }
        notifyObservers();
    }


    public boolean sellProduct(Product product, int quantity, Buyer buyer) {
        Optional<ProductOffer> offerOpt = offers.stream()
                .filter(o -> o.getProduct().equals(product))
                .findFirst();

        if (offerOpt.isPresent()) {
            ProductOffer offer = offerOpt.get();
            if (offer.getAvailableQuantity() >= quantity) {
                double totalCost = quantity * offer.getPrice();
                offer.decreaseQuantity(quantity);
                double profit = quantity * (offer.getPrice() - offer.getProduct().getProductionCost());
                totalProfit += profit;

                String logEntry = String.format("Transakcja: %s kupił %d szt. %s za %.2f (zysk: %.2f)",
                        buyer, quantity, product.getName(), totalCost, profit);
                transactionLog.add(logEntry);

                System.out.println(logEntry);

                return true;
            }
        }
        System.out.println("Brak możliwości sprzedaży " + product.getName() + " dla " + buyer);

        return false;
    }

    public void zeroTotalProfit(){
        this.totalProfit=0;
    }

    public void payInflationTax(double taxAmount) {
        totalProfit -= taxAmount;
        System.out.println(this + " zapłacił podatek inflacyjny: " +
                String.format("%.2f", taxAmount) +
                ", zysk po opodatkowaniu: " + String.format("%.2f", totalProfit));

    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
        for (ProductOffer offer : offers) {
            double newPrice = offer.getProduct().getProductionCost() * (1 + profitMargin);
            offer.setPrice(newPrice);
        }
        if (detailedLogging) {
            System.out.println("Zmieniono marżę sprzedawcy na: " +
                    String.format("%.2f", profitMargin * 100) + "%");
        }
    }


    public List<String> getTransactionLog() {
        return Collections.unmodifiableList(transactionLog);
    }


    public void setInflation(double inflation) {
        this.currentInflation = inflation;
    }

    @Override
    public void update(double inflation) {
        System.out.println("Sprzedawca otrzymał powiadomienie o zmianie inflacji");
        this.currentInflation = inflation;
        setProfitMargin();
//        adjustPrices(currentInflation);
    }


    public void accept(PriceUpdateVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Sprzedawca (marża: " + String.format("%.2f", profitMargin * 100) + "%)";
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(ProductOffer changedOffer) {
        for (Observer o : observers) {
            o.update(changedOffer);
        }
    }

//    public void changePrice(Product product, double newPrice) {
//        ProductOffer offer = findOffer(product);
//        offer.setPrice(newPrice);
//        notifyObservers(offer); // <<< ważne
//    }

    public Optional<ProductOffer> findOffer(Product product) {
        return offers.stream()
                .filter(offer -> offer.getProduct().equals(product))
                .findFirst();
    }


    @Override
    public void update(ProductOffer offer) {
        // Dostosuj logikę do roli Sellera jako obserwatora (jeśli to ma sens)
    }

    public void notifyObservers(){
        System.out.println("Zmiana cen");
    }


    public void update(Observable o, double l){

    }

    public void setProfitMargin() {

        if (totalProfit == 0.0){
            this.profitMargin = 0.2;
        }
        else if (totalProfit < purpose){
            this.profitMargin += 0.1;

        }else if(! (purpose*1.5 > totalProfit && totalProfit > purpose*1.1)){
            this.profitMargin -= 0.2;
        }
        System.out.println("AKT marza"+this.profitMargin);


    }

    public void zeroTotal(){
        totalProfit=0;
    }

    public double getTotal(){
        return totalProfit;
    }

    public void setMargin(double m){
        this.profitMargin = m;
    }


}

