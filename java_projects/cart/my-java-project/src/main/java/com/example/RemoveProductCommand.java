package com.example;


public class RemoveProductCommand implements Command {
    private final Cart cart;
    private final Product product;
    private int productIndex = -1; 

    public RemoveProductCommand(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }

    @Override
    public void execute() {
        Product[] products = cart.getProducts();

        ProductValidator.validateProductInCart(products, product);

        for (int i = 0; i < products.length; i++) {
            if (products[i].equals(product)) {
                this.productIndex = i;
                break;
            }
        }

        Product[] newProducts = new Product[products.length - 1];
        System.arraycopy(products, 0, newProducts, 0, productIndex);
        System.arraycopy(products, productIndex + 1, newProducts, productIndex, products.length - productIndex - 1);
        cart.setProducts(newProducts);
    }

    @Override
    public void undo() {
        if (productIndex == -1) {
            throw new IllegalStateException("Nie można cofnąć operacji. Produkt nie został usunięty.");
        }

        Product[] currentProducts = cart.getProducts();
        Product[] newProducts = new Product[currentProducts.length + 1];

        System.arraycopy(currentProducts, 0, newProducts, 0, productIndex);
        newProducts[productIndex] = product;
        System.arraycopy(currentProducts, productIndex, newProducts, productIndex + 1, currentProducts.length - productIndex);

        cart.setProducts(newProducts);
    }
}
