package com.example.discounts;

import com.example.Discount;
import com.example.Product;

public class CouponDiscountForOneProduct implements Discount {
    private Product product; 
    private double discountRate;

    public CouponDiscountForOneProduct(Product product, double discountRate) {
        if (discountRate < 0) {
            throw new IllegalArgumentException("Rabat nie może być ujemny.");
        }
        this.product = product;
        this.discountRate = discountRate;
    }

    @Override
    public Product[] apply(Product[] products) {
        for (Product p : products) {
            if (p.getCode().equals(product.getCode())) { 
                double discountedPrice = p.getPrice() * (1 - discountRate);
                p.setDiscountPrice(discountedPrice); 
                break;
            }
        }
        return products;
    }

    @Override
    public String getDescription() {
        return "Coupon discount " + (discountRate * 100) + "% on " + product.getName();
    }
}
