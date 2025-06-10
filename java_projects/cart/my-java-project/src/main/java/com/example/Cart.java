package com.example;

import java.util.Arrays;
import java.util.Comparator;

import com.example.searching.ProductSearchStrategy;
import com.example.sorting.PriceThenNameSortingStrategy;
import com.example.sorting.ProductSortingStrategy;

public class Cart {
    private Product[] products;
    private Discount[] discounts;
    private ProductSortingStrategy sortingService;
    private static final ProductCountValidator countValidator = new ProductCountValidator();
    private final CartInvoker invoker = new CartInvoker();

    // Konstruktor domyślny
    public Cart() {
        this.products = new Product[0];
        this.discounts = new Discount[0];
        this.sortingService = new PriceThenNameSortingStrategy();
    }

    // Konstruktor z produktami
    public Cart(Product[] products) {
        this.products = Arrays.copyOf(products, products.length);
        this.discounts = new Discount[0];
        this.sortingService = new PriceThenNameSortingStrategy();
    }

    // Konstruktor z produktami i rabatami
    public Cart(Product[] products, Discount[] discounts) {
        this.products = Arrays.copyOf(products, products.length);
        this.discounts = Arrays.copyOf(discounts, discounts.length);
        this.sortingService = new PriceThenNameSortingStrategy();
    }

    // Metoda walidująca, czy koszyk nie jest pusty
    private void validateCart() {
        countValidator.isNotEmpty(products);
    }

    private void validateDiscounts() {
        countValidator.isNotEmptyD(discounts);
    }


    // Dodawanie produktu przez Command
    public void addProduct(Product product) {
        validateCart(); // Sprawdzenie pustego koszyka
        invoker.executeCommand(new AddProductCommand(this, product));
    }

    // Usuwanie produktu przez Command
    public void removeProduct(Product product) {
        validateCart(); // Sprawdzenie pustego koszyka
        invoker.executeCommand(new RemoveProductCommand(this, product));
    }

    // Dodawanie rabatu (Command)
    public void addDiscount(Discount discount) {
        invoker.executeCommand(new AddDiscountCommand(this, discount));
    }

    // Usuwanie rabatu przez Command
    public void removeDiscount(Discount discount) {
        invoker.executeCommand(new RemoveDiscountCommand(this, discount));
    }

    // Zastosowanie rabatów
    public void applyDiscounts() {
        validateCart(); 
        validateDiscounts();
        ApplyBestDiscountOrderCommand applyBestDiscountOrderCommand = new ApplyBestDiscountOrderCommand(this, this.discounts);
        applyBestDiscountOrderCommand.execute();
    }

    // Obsługa historii Command
    public void undoLastAction() {
        invoker.undoLastCommand();
    }

    public void redoLastUndoneAction() {
        invoker.redoLastUndoneCommand();
    }

    // Obliczanie ceny
    public double getTotalPrice() {
        validateCart(); 
        return Arrays.stream(products)
                   .mapToDouble(Product::getDiscountPrice)
                   .sum();
    }

    public double getBaseTotalPrice() {
        validateCart(); 
        return Arrays.stream(products)
                   .mapToDouble(Product::getPrice)
                   .sum();
    }

    // Wyszukiwanie produktów
    public Product[] searchProducts(ProductSearchStrategy strategy, int n) {
        validateCart();
        return strategy.search(products, n);
    }

    // Sortowanie produktów według wybranej strategii
    public void sortProducts() {
        validateCart(); 
        Arrays.sort(products, sortingService.getComparator());
    }

    // Metoda zmieniająca strategię sortowania w trakcie działania programu
    public void setSortingStrategy(ProductSortingStrategy sortingService) {
        this.sortingService = sortingService;
    }

    // Gettery i settery
    public Product[] getProducts() {
        validateCart(); 
        Product[] sortedProducts = Arrays.copyOf(products, products.length);
        Arrays.sort(sortedProducts, sortingService.getComparator());
        return sortedProducts;
    }

    protected void setProducts(Product[] products) {
        this.products = Arrays.copyOf(products, products.length);
    }

    public Discount[] getDiscounts() {
        return Arrays.copyOf(discounts, discounts.length);
    }

    protected void setDiscounts(Discount[] discounts) {
        this.discounts = Arrays.copyOf(discounts, discounts.length);
    }

    // Metoda do znalezienia najtańszego produktu
    public Product getCheapestProduct() {
        validateCart(); 
        return Arrays.stream(products)
                .min(Comparator.comparingDouble(Product::getPrice))  
                .orElse(null);  
    }

    // Metoda do znalezienia najdroższego produktu
    public Product getMostExpensiveProduct() {
        validateCart(); 
        return Arrays.stream(products)
                .max(Comparator.comparingDouble(Product::getPrice))  
                .orElse(null);  
    }

    // Czyszczenie koszyka
    public void clearCart() {
        this.products = new Product[0];
        this.discounts = new Discount[0];
        this.invoker.clearHistory();
    }
}
