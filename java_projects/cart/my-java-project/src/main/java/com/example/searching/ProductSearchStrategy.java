package com.example.searching;

import com.example.Product;

public interface ProductSearchStrategy {
    Product[] search(Product[] products, int n);
}
