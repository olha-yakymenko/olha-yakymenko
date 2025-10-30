package com.example;

public interface Discount {
    Product[] apply(Product[] products);  
    String getDescription();

}