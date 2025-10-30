package com.example;
import java.util.Arrays;

public class AddDiscountCommand implements Command {
    private final Cart cart;
    private final Discount discount;

    public AddDiscountCommand(Cart cart, Discount discount) {
        this.cart = cart;
        this.discount = discount;
    }

    @Override
    public void execute() {
        Discount[] newDiscounts = Arrays.copyOf(cart.getDiscounts(), cart.getDiscounts().length + 1);
        newDiscounts[newDiscounts.length - 1] = discount;
        cart.setDiscounts(newDiscounts);
    }

    @Override
    public void undo() {
        cart.removeDiscount(discount);
    }
}
