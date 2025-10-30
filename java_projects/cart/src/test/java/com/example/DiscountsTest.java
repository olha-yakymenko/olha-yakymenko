package com.example;

import org.junit.jupiter.api.Test;

import com.example.discounts.BuyTwoGetOneFree;
import com.example.discounts.CouponDiscountForOneProduct;
import com.example.discounts.FivePercentDiscount;
import com.example.discounts.FreeGiftDiscount;
import com.example.discounts.TotalValueDiscount;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;

class DiscountsTest {

    @Test
    public void testApplyFivePercentDiscountWithTotalAboveThreshold() {
        Product[] products = {
            new Product("P001", "Laptop", 2000),
            new Product("P002", "Phone", 1200),
            new Product("P003", "Headphones", 100)
        };
        
        Cart cart = new Cart(products);
        Discount discount = new FivePercentDiscount();
        cart.addDiscount(discount);
        cart.applyDiscounts();
        
        double expectedPrice = (2000 + 1200 + 100) * 0.95; 
        double totalDiscountedPrice = Arrays.stream(products)
                                            .mapToDouble(Product::getDiscountPrice)
                                            .sum();
        
        assertEquals(expectedPrice, totalDiscountedPrice, 0.01);
    }

    @Test
    public void testApplyFivePercentDiscountWithTotalBelowThreshold() {
        Product[] products = { new Product("P003", "Headphones", 100) };
        
        Cart cart = new Cart(products);
        Discount discount = new FivePercentDiscount();
        cart.addDiscount(discount);
        cart.applyDiscounts();
        
        double totalDiscountedPrice = Arrays.stream(products)
                                            .mapToDouble(Product::getDiscountPrice)
                                            .sum();
        
        assertEquals(100.0, totalDiscountedPrice, 0.01);
    }

    @Test
    void testApplyBuyTwoGetOneFreeDiscount() {
        Product[] products = {
            new Product("P001", "Laptop", 2000),
            new Product("P002", "Phone", 1200),
            new Product("P003", "Headphones", 100)
        };
        
        Cart cart = new Cart(products);
        Discount discount = new BuyTwoGetOneFree();
        cart.addDiscount(discount);
        cart.applyDiscounts();

        Product[] discountedProducts = cart.getProducts();
        Arrays.sort(discountedProducts, Comparator.comparingDouble(Product::getPrice));

        assertEquals(0.0, discountedProducts[0].getDiscountPrice(), "The cheapest product should be free.");
        assertEquals(1200.0, discountedProducts[1].getDiscountPrice(), "The second product should remain the same.");
        assertEquals(2000.0, discountedProducts[2].getDiscountPrice(), "The third product should remain the same.");
    }

    @Test
    void testApplyBuyTwoGetOneFreeDiscountWithLessThanThreeProducts() {
        Product[] products = {
            new Product("P001", "Laptop", 2000),
            new Product("P002", "Phone", 1200)
        };
        
        Cart cart = new Cart(products);
        Discount discount = new BuyTwoGetOneFree();
        cart.addDiscount(discount);
        cart.applyDiscounts();

        Product[] discountedProducts = cart.getProducts();
        assertEquals(2000.0, discountedProducts[0].getDiscountPrice(), "No discount should be applied.");
        assertEquals(1200.0, discountedProducts[1].getDiscountPrice(), "No discount should be applied.");
    }

    @Test
    void testFreeGiftDiscountApplied() {
        Product[] products = {
            new Product("P1", "Item1", 150.0),
            new Product("P2", "Item2", 100.0)
        };
        
        Product gift = new Product("G1", "Gift", 0.0);
        Discount discount = new FreeGiftDiscount(gift, 200.0);
        Cart cart = new Cart(products);
        
        cart.addDiscount(discount);
        cart.applyDiscounts();
        
        Product[] updatedProducts = cart.getProducts();
        assertEquals(products.length + 1, updatedProducts.length);
        assertEquals("Gift", updatedProducts[updatedProducts.length - 1].getName());
    }

    @Test
    void testFreeGiftDiscountNotAppliedBelowThreshold() {
        Product[] products = {
            new Product("P1", "Item1", 90.0),
            new Product("P2", "Item2", 100.0)
        };
        
        Product gift = new Product("G1", "Gift", 0.0);
        Discount discount = new FreeGiftDiscount(gift, 200.0);
        Cart cart = new Cart(products);
        
        cart.addDiscount(discount);
        cart.applyDiscounts();
        
        Product[] updatedProducts = cart.getProducts();
        assertEquals(products.length, updatedProducts.length);
    }

    @Test
    void testCouponDiscountForOneProductApplied() {
        Product[] products = {
            new Product("P1", "Item1", 50.0),
            new Product("P2", "Item2", 100.0),
            new Product("P3", "Laptop", 2000.0)
        };
        
        Product targetProduct = new Product("P3", "Laptop", 2000.0);
        double discountRate = 0.2;

        CouponDiscountForOneProduct discount = new CouponDiscountForOneProduct(targetProduct, discountRate);
        Cart cart = new Cart(products);
        
        cart.addDiscount(discount);
        cart.applyDiscounts();

        assertEquals(1750.0, cart.getTotalPrice(), "Discounted price should be 1750.0");
    }

    @Test
    void testCouponDiscountForOneProductNotAppliedToDifferentProduct() {
        Product targetProduct = new Product("P1", "Laptop", 2000.0);
        Product otherProduct = new Product("P2", "Phone", 1000.0);
        double discountRate = 0.3;

        CouponDiscountForOneProduct discount = new CouponDiscountForOneProduct(targetProduct, discountRate);
        Cart cart = new Cart(new Product[] {otherProduct});
        
        cart.addDiscount(discount);
        cart.applyDiscounts();

        assertEquals(1000.0, otherProduct.getDiscountPrice(), "Discount should not be applied to other products");
    }

    @Test
    void testTotalValueDiscountApplied() {
        Product[] products = {
            new Product("P1", "Item1", 100.0),
            new Product("P2", "Item2", 200.0)
        };
        
        TotalValueDiscount discount = new TotalValueDiscount(250.0, 10.0);
        Cart cart = new Cart(products);
        
        cart.addDiscount(discount);
        cart.applyDiscounts();

        assertEquals(90.0, products[0].getDiscountPrice(), 0.001);
        assertEquals(180.0, products[1].getDiscountPrice(), 0.001);
    }

    @Test
    void testTotalValueDiscountNotApplied() {
        Product[] products = {
            new Product("P1", "Item1", 100.0),
            new Product("P2", "Item2", 100.0)
        };
        
        TotalValueDiscount discount = new TotalValueDiscount(250.0, 10.0);
        Cart cart = new Cart(products);
        
        cart.addDiscount(discount);
        cart.applyDiscounts();

        assertEquals(100.0, products[0].getDiscountPrice(), 0.001);
        assertEquals(100.0, products[1].getDiscountPrice(), 0.001);
    }
}
