package com.example.sorting;

import com.example.Product;

import java.util.Comparator;

public class PriceThenNameSortingStrategy implements ProductSortingStrategy {

    @Override
    public Comparator<Product> getComparator() {
        return (product1, product2) -> {
            int priceComparison = Double.compare(product2.getPrice(), product1.getPrice());
            if (priceComparison != 0) {
                return priceComparison;  
            }
            
            return product1.getName().compareTo(product2.getName());
        };
    }
}
