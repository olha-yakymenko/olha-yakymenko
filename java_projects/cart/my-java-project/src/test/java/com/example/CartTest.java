package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.discounts.BuyTwoGetOneFree;
import com.example.discounts.FivePercentDiscount;
import com.example.discounts.PercentageDiscount;
import com.example.searching.MostExpensiveSearchStrategy;
import com.example.searching.ProductSearchStrategy;
import com.example.sorting.NameAscendingSortingStrategy;
import com.example.sorting.PriceDescendingSortingStrategy;
import com.example.sorting.ProductSortingStrategy;
import static com.example.ProductValidator.isProductInCart;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class CartTest {
    private Cart cart;
    private Product laptop;
    private Product phone;
    private Product headphones;

    @BeforeEach
    public void setUp() {
        laptop = new Product("P001", "Laptop", 3000);
        phone = new Product("P002", "Phone", 1500);
        headphones = new Product("P003", "Headphones", 200);
        
        Product[] products = {laptop, phone, headphones};
        Discount[] discounts = {new FivePercentDiscount()};
        cart = new Cart(products, discounts);
    }

    // ==================== TESTY DODAWANIA/USUWANIA PRODUKTÓW ====================
    @Test
    public void testAddProduct() {
        Product tablet = new Product("P004", "Tablet", 1200);
        cart.addProduct(tablet);
        assertTrue(isProductInCart(cart.getProducts(), tablet));
    }

    @Test
    public void testRemoveProduct() {
        cart.removeProduct(phone);
        assertEquals(2, cart.getProducts().length);
        assertFalse(Arrays.asList(cart.getProducts()).contains(phone));
    }

    @Test
    public void testRemoveNonExistentProduct() {
        Product nonExistentProduct = new Product("P999", "NonExistent", 500);
        assertThrows(IllegalArgumentException.class, () -> cart.removeProduct(nonExistentProduct));
    }

    @Test
    public void testClearCart() {
        cart.clearCart();
        assertThrows(IllegalStateException.class, () -> cart.getProducts());
    }

    // ==================== TESTY CENY/CALCULATIONS ====================
    @Test
    public void testTotalPriceCalculation() {
        double expectedTotal = laptop.getPrice() + phone.getPrice() + headphones.getPrice();
        assertEquals(expectedTotal, cart.getTotalPrice(), 0.01);
    }

    @Test
    public void testGetCheapestProduct() {
        assertEquals(headphones, cart.getCheapestProduct());
    }

    @Test
    public void testGetMostExpensiveProduct() {
        assertEquals(laptop, cart.getMostExpensiveProduct());
    }

    @Test
    public void testGetCheapestProductWhenEmpty() {
        cart.clearCart();
        assertThrows(IllegalStateException.class, () -> cart.getCheapestProduct());
    }

    @Test
    public void testGetMostExpensiveProductWhenEmpty() {
        cart.clearCart();
        assertThrows(IllegalStateException.class, () -> cart.getMostExpensiveProduct());
    }

    // ==================== TESTY RABATÓW ====================
    @Test
    public void testApplyPercentageDiscount() {
        cart.addDiscount(new PercentageDiscount(5));
        cart.applyDiscounts();
        assertEquals(4465, cart.getTotalPrice(), 0.01);
    }

    @Test
    public void testMultipleDiscounts() {
        cart.addDiscount(new PercentageDiscount(10));
        cart.addDiscount(new FivePercentDiscount());
        cart.applyDiscounts();
        double expectedPrice = 4700 * 0.9 * 0.95;
        assertEquals(expectedPrice, cart.getTotalPrice(), 0.01);
    }

    @Test
    public void testApplyBestDiscounts() {
        cart.addDiscount(new PercentageDiscount(10));
        cart.addDiscount(new FivePercentDiscount());
        cart.addDiscount(new BuyTwoGetOneFree());
        cart.applyDiscounts();

        assertEquals(3847.5, cart.getTotalPrice(), 0.01);
    }

    @Test
    public void testApplyNegativeDiscount() {
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(-10));
    }

    @Test
    public void testApplyDiscountsOnEmptyCart() {
        cart.clearCart();
        cart.addDiscount(new PercentageDiscount(10));
        assertThrows(IllegalStateException.class, () -> cart.applyDiscounts());
    }

    @Test
    public void testAddDiscountToEmptyCart() {
        Cart emptyCart = new Cart();
        emptyCart.addDiscount(new PercentageDiscount(10));
        assertThrows(IllegalStateException.class, () -> emptyCart.applyDiscounts());
    }

    // ==================== TESTY SORTOWANIA ====================
    @Test
    public void testSortProductsByPriceDescending() {
        ProductSortingStrategy sortingStrategy = new PriceDescendingSortingStrategy();
        cart.setSortingStrategy(sortingStrategy);
        cart.sortProducts();
        
        Product[] sortedProducts = cart.getProducts();
        assertEquals(laptop, sortedProducts[0]);
        assertEquals(phone, sortedProducts[1]);
        assertEquals(headphones, sortedProducts[2]);
    }

    @Test
    public void testSortProductsByNameAscendingSortingStrategy() {
        ProductSortingStrategy sortingStrategy = new NameAscendingSortingStrategy();
        cart.setSortingStrategy(sortingStrategy);
        cart.sortProducts();

        Product[] sortedProducts = cart.getProducts();
        assertEquals(headphones, sortedProducts[0]);
        assertEquals(laptop, sortedProducts[1]);
        assertEquals(phone, sortedProducts[2]);
    }

    @Test
    void testDefaultSorting() {
        Product[] sortedProducts = cart.getProducts();
        assertEquals(laptop, sortedProducts[0]);
        assertEquals(phone, sortedProducts[1]);
        assertEquals(headphones, sortedProducts[2]);
    }

    @Test
    void testSortingAfterChangingStrategy() {
        ProductSortingStrategy sortingStrategy = new NameAscendingSortingStrategy();
        cart.setSortingStrategy(sortingStrategy);
        Product[] sortedProducts = cart.getProducts();
        assertEquals(headphones, sortedProducts[0]);
        assertEquals(laptop, sortedProducts[1]);
        assertEquals(phone, sortedProducts[2]);
    }

    // ==================== TESTY WYSZUKIWANIA ====================
    @Test
    public void testSearchMostExpensiveProduct() {
        ProductSearchStrategy expensiveSearchService = new MostExpensiveSearchStrategy();
        Product[] mostExpensiveProduct = cart.searchProducts(expensiveSearchService, 1);
        assertNotNull(mostExpensiveProduct);
        assertEquals(1, mostExpensiveProduct.length);
        assertEquals(laptop, mostExpensiveProduct[0]);
    }

    // ==================== TESTY COFANIA I PONAWIANIA OPERACJI ====================

    @Test
    public void testUndoAddProduct() {
        Product tablet = new Product("P004", "Tablet", 1200);
        cart.addProduct(tablet);

        cart.undoLastAction();
        assertFalse(isProductInCart(cart.getProducts(), tablet));
    }

    @Test
    public void testUndoRemoveProduct() {
        cart.removeProduct(phone);
        cart.undoLastAction();
        assertTrue(isProductInCart(cart.getProducts(), phone));
    }

    @Test
    public void testRedoRemoveProduct() {
        cart.removeProduct(phone);
        cart.undoLastAction();
        cart.redoLastUndoneAction();

        assertFalse(isProductInCart(cart.getProducts(), phone));
    }

    @Test
    public void testUndoAddDiscount() {
        Discount discount = new PercentageDiscount(10);
        cart.addDiscount(discount);
        cart.undoLastAction();
        assertEquals(1, cart.getDiscounts().length);
    }

    @Test
    public void testRedoAddDiscount() {
        Discount discount = new PercentageDiscount(10);
        cart.addDiscount(discount);
        cart.undoLastAction();
        cart.redoLastUndoneAction();

        assertEquals(2, cart.getDiscounts().length);
    }

    @Test
    public void testUndoRemoveDiscount() {
        Discount discount = new PercentageDiscount(10);
        cart.addDiscount(discount);
        cart.removeDiscount(discount);
        cart.undoLastAction();
        assertEquals(2, cart.getDiscounts().length);
    }

    @Test
    public void testRedoRemoveDiscount() {
        Discount discount = new PercentageDiscount(10);
        cart.addDiscount(discount);
        cart.removeDiscount(discount);
        cart.undoLastAction();
        cart.redoLastUndoneAction();

        assertEquals(1, cart.getDiscounts().length);
    }

    @Test
    public void testRedoClearCart() {
        cart.clearCart();
        cart.undoLastAction();
        cart.redoLastUndoneAction();

        assertThrows(IllegalStateException.class, () -> cart.getProducts());
    }

}