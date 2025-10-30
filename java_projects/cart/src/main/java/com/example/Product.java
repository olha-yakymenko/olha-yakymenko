package com.example;

public class Product implements Comparable<Product> {
    private String code;  
    private String name;  
    private double price; 
    private double discountPrice;

    public Product(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = price; 
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    @Override
    public int compareTo(Product other) {
        int priceComparison = Double.compare(this.discountPrice, other.discountPrice);
        if (priceComparison != 0) {
            return priceComparison; 
        }
        return this.name.compareTo(other.name); 
    }

}
