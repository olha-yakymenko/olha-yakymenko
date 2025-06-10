package com.example.discounts;

import com.example.Discount;
import com.example.Product;

public class PercentageDiscount implements Discount {
    private double percentage;  
    
    public PercentageDiscount(double percentage) {
        if (percentage < 0) {
            throw new IllegalArgumentException("Rabat nie może być ujemny.");
        }
        this.percentage = percentage;
    }

    @Override
    public Product[] apply(Product[] products) {
        for (Product product : products) {
            double discountAmount = product.getPrice() * (percentage / 100);
            double newPrice = product.getPrice() - discountAmount;
            product.setDiscountPrice(newPrice);  
        }
        return products;
    }

    @Override
    public String getDescription() {
        return String.format("%.2f%% discount on all products", percentage);
    }
}
