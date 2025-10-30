package com.example;
 
import java.util.Arrays;

public class ApplyBestDiscountOrderCommand implements Command {
    private final Cart cart;
    private final Discount[] availableDiscounts;
    private Product[] originalProducts;
    private Discount[] originalDiscounts;

    public ApplyBestDiscountOrderCommand(Cart cart, Discount[] availableDiscounts) {
        this.cart = cart;
        this.availableDiscounts = Arrays.copyOf(availableDiscounts, availableDiscounts.length);
    }

    @Override
    public void execute() {
        this.originalProducts = cart.getProducts();
        this.originalDiscounts = cart.getDiscounts();
        
        Discount[] bestOrder = Arrays.copyOf(availableDiscounts, availableDiscounts.length);
        
        cart.setDiscounts(bestOrder);
        
        Product[] currentProducts = cart.getProducts();
        for (Discount discount : bestOrder) {
            currentProducts = discount.apply(currentProducts);
        }
        cart.setProducts(currentProducts);
    }

    @Override
    public void undo() {
        cart.setProducts(originalProducts);
        cart.setDiscounts(originalDiscounts);
    }
}