package com.example.sorting;

import java.util.Comparator;

import com.example.Product;

public class PriceDescendingSortingStrategy implements ProductSortingStrategy {
    @Override
    public Comparator<Product> getComparator() {
        return Comparator.comparingDouble(Product::getDiscountPrice).reversed();
    }
}
