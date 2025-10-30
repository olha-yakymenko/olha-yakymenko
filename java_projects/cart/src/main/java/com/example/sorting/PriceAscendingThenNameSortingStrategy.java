package com.example.sorting;

import java.util.Comparator;

import com.example.Product;

public class PriceAscendingThenNameSortingStrategy implements ProductSortingStrategy {
    @Override
    public Comparator<Product> getComparator() {
        return Comparator.comparingDouble(Product::getDiscountPrice)
                         .thenComparing(Product::getName);
    }
}
