package com.example.discounts;
import java.util.Arrays;
import java.util.Comparator;

import com.example.Discount;
import com.example.Product;

// public class BuyTwoGetThreeCheapestFreePromotion implements Promotion {
//     @Override
//     public void apply(List<Product> products) {
//         if (products.size() >= 2) {
//             products.sort(Comparator.comparingDouble(Product::getPrice)); // Sortowanie po cenie rosnąco
//             Product cheapest = products.get(2); // Trzeci najtańszy produkt
//             cheapest.setDiscountPrice(0); // Gratis
//         }
//     }
// }



public class BuyTwoGetOneFree implements Discount {
    @Override
    public Product[] apply(Product[] products) {
        if (products.length >= 3) {
            Product[] sorted = Arrays.copyOf(products, products.length);
            
            Arrays.sort(sorted, Comparator.comparingDouble(Product::getPrice));
            
            sorted[0].setDiscountPrice(0);
            return sorted;
        }
        return products;
    }

    @Override
    public String getDescription() {
        return "Buy 2 products, get 3rd cheapest free";
    }

}