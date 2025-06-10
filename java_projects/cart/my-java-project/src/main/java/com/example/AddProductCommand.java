package com.example;

import java.util.Arrays;

public class AddProductCommand implements Command {
    private final Cart cart;
    private final Product product;
    private boolean executed = false;

    public AddProductCommand(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }

    @Override
    public void execute() {
        if (!executed) {
            Product[] currentProducts = cart.getProducts();
            Product[] newProducts = Arrays.copyOf(currentProducts, currentProducts.length + 1);
            newProducts[newProducts.length - 1] = product;
            cart.setProducts(newProducts);
            executed = true;
        }
    }

    @Override
    public void undo() {
        cart.removeProduct(product);
    }
}