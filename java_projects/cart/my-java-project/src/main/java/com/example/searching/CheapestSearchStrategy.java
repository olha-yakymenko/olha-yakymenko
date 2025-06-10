package com.example.searching;

import java.util.Arrays;
import java.util.Comparator;

import com.example.Product;

public class CheapestSearchStrategy implements ProductSearchStrategy {
    @Override
    public Product[] search(Product[] products, int n) {
        return Arrays.stream(products)
                     .sorted(Comparator.comparingDouble(Product::getDiscountPrice))
                     .limit(n)
                     .toArray(Product[]::new);
    }
}
