package com.example.sorting;

import java.util.Comparator;

import com.example.Product;

public class NameAscendingSortingStrategy implements ProductSortingStrategy {
    @Override
    public Comparator<Product> getComparator() {
        return Comparator.comparing(Product::getName);
    }
}
