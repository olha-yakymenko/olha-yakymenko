package com.example.discounts;

import java.util.Arrays;

import com.example.Discount;
import com.example.Product;

public class FreeGiftDiscount implements Discount {
    private Product gift; 
    private double threshold;

    public FreeGiftDiscount(Product gift, double threshold) {
        this.gift = gift;
        this.threshold = threshold;
    }

    @Override
    public Product[] apply(Product[] products) {
    double total = Arrays.stream(products)
                         .mapToDouble(Product::getPrice)
                         .sum();

    if (total > threshold) {
        Product[] newProducts = Arrays.copyOf(products, products.length + 1);
        
        newProducts[products.length] = gift;

        return newProducts;
    }
    return products;
}



    @Override
    public String getDescription() {
        return "Free " + gift.getName() + " when total > " + threshold + " zł";
    }
}
