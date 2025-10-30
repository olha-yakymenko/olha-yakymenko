package com.example;

public class ProductCountValidator {
    public boolean isNotEmpty(Product[] products) {
        if (products == null || products.length == 0) {
            throw new IllegalStateException("Koszyk jest pusty.");
        }
        return true;
    }

    public boolean isNotEmptyD(Discount[] discounts) {
        if (discounts == null || discounts.length == 0) {
            throw new IllegalStateException("Brak rabatów");
        }
        return true;
    }
}
