package org.example;

class ProductOffer {
    private final Product product;
    private double price;
    private int availableQuantity;

    public ProductOffer(Product product, double price, int availableQuantity) {
        this.product = product;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public double getBasePrice(){ return product.getProductionCost();}

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void decreaseQuantity(int amount) {
        this.availableQuantity = Math.max(0, this.availableQuantity - amount);
    }

    @Override
    public String toString() {
        return product.getName() + " - cena: " + price + ", ilość: " + availableQuantity;
    }
}
