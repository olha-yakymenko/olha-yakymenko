package com.example.discounts;

import java.util.Arrays;

import com.example.Discount;
import com.example.Product;

public class FivePercentDiscount implements Discount {
    private static final double discount_threshold = 300.0; 
    private static final double discount_rate = 0.05;

    @Override
    public Product[] apply(Product[] products) {
        double totalPrice = Arrays.stream(products)
                                  .mapToDouble(Product::getDiscountPrice) 
                                  .sum();

        if (totalPrice > discount_threshold) {
            double totalDiscount = totalPrice * discount_rate;

            for (Product product : products) {
                double productDiscount = (product.getDiscountPrice() / totalPrice) * totalDiscount; 
                double newPrice = product.getDiscountPrice() - productDiscount; 
                product.setDiscountPrice(newPrice);  
                System.out.println("New Discount Price: " + product.getDiscountPrice());  
            }
        }
        return products;
    }

    @Override
    public String getDescription() {
        return "5% discount on all products if total price exceeds 300 zł";
    }
}

