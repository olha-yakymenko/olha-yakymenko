package com.example;
import java.util.Arrays;
public class RemoveDiscountCommand implements Command {
    private final Cart cart;
    private final Discount discount;

    public RemoveDiscountCommand(Cart cart, Discount discount) {
        this.cart = cart;
        this.discount = discount;
    }

    @Override
    public void execute() {
        Discount[] newDiscounts = Arrays.stream(cart.getDiscounts())
            .filter(d -> !d.equals(discount))
            .toArray(Discount[]::new);
        cart.setDiscounts(newDiscounts);
    }

    @Override
    public void undo() {
        cart.addDiscount(discount);
    }
}
