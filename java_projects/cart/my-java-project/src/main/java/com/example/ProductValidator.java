package com.example;

public class ProductValidator {
    public static boolean isProductInCart(Product[] products, Product product) {
        for (Product p : products) {
            if (p != null && p.getCode().equals(product.getCode())) { 
                return true;
            }
        }
        return false;
    }

    public static void validateProductInCart(Product[] products, Product product) {
        if (!isProductInCart(products, product)) {
            throw new IllegalArgumentException("Produkt " + product.getName() + " nie został znaleziony w koszyku.");
        }
    }
}
