package com.example.sorting;

import java.util.Comparator;

import com.example.Product;

public interface ProductSortingStrategy {
    Comparator<Product> getComparator();
}

