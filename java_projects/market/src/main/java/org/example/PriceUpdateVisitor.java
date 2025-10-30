

package org.example;

class PriceUpdateVisitor implements Visitor {
    private final double inflation;

    public PriceUpdateVisitor(double inflation) {
        this.inflation = inflation;
    }

    @Override
    public void visit(Seller seller) {
        System.out.println("Aktualizacja cen dla " + seller);
        seller.adjustPrices();
    }

    @Override
    public void visit(Buyer buyer) {
        // Kupujący stają się bardziej wrażliwi na ceny przy wysokiej inflacji
        double impactFactor = Math.min(inflation * 2, 0.5); // Maksymalnie 50% zmiany
        buyer.adjustPriceSensitivity(impactFactor);
    }

}