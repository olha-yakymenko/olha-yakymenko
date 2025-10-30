package org.example;

public interface EconomicObserver {
    void onPurchase(Buyer buyer, Product product, int quantity, double pricePerUnit);
    void updateBudget(Buyer buyer, double budget); // << DODAJ TO
}
