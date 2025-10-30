package com.example.discounts;
import java.util.Arrays;

import com.example.Discount;
import com.example.Product;

public class TotalValueDiscount implements Discount {
    private double threshold;
    private double percentage;

    public TotalValueDiscount(double threshold, double percentage) {
        this.threshold = threshold;
        this.percentage = percentage;
    }

    @Override
    public Product[] apply(Product[] products) {
        double total = Arrays.stream(products).mapToDouble(Product::getPrice).sum();
        if (total > threshold) {
            for (Product p : products) {
                p.setDiscountPrice(p.getPrice() * (1 - percentage/100));
            }
            return products;
        }
        return products;
    }

    @Override
    public String getDescription() {
        return percentage + "% discount when total > " + threshold + " zł";
    }
}