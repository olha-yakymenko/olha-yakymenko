
package org.example;

import org.junit.jupiter.api.Test;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MarketStabilityTests {

    private void displayAndWaitForChart(XYChart chart, String title) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
            JFrame frame = wrapper.displayChart();
            frame.setTitle(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    latch.countDown();
                }
            });
        }).start();

        assertTrue(latch.await(60, TimeUnit.SECONDS), "Wykres nie został zamknięty w wymaganym czasie");
    }

    private XYChart createInflationChart(MarketModel model, String title) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(title)
                .xAxisTitle("Tura")
                .yAxisTitle("Inflacja (%)")
                .build();

        List<Double> inflationHistory = model.getCentralBank().getInflationPercentages();
        List<Integer> turns = IntStream.range(0, inflationHistory.size())
                .boxed()
                .toList();

        chart.addSeries("Inflacja", turns, inflationHistory);
        return chart;
    }

    @Test
    void testBasicStability() throws InterruptedException {
        MarketModel model = new MarketModel(0.05, true);

        Product product = new Product("Mleko", Product.Type.NECESSITY, 1.5, 5);
        Seller seller = new Seller(0.10, true);
        seller.addProductOffer(product, 100);
        model.addSeller(seller);

        Buyer buyer = new Buyer(200.0, 20.0, 0.5);
        buyer.addNeed(product, 2);
        model.addBuyer(buyer);

        while (!model.getCentralBank().checkStability() && model.getCurrentTurn() < 100) {
            model.nextTurn();
        }
        for (int i = 0; i < 30; i++) model.nextTurn();

        assertTrue(model.getCentralBank().checkStability());
        displayAndWaitForChart(createInflationChart(model, "Podstawowa stabilizacja"), "Test 1");
    }

    //zmiana marzy
    @Test
    void testProfitMarginChange() throws InterruptedException {
        MarketModel model = new MarketModel(0.03, true);

        Product product = new Product("Jajka", Product.Type.NECESSITY, 1.0, 8);
        Seller seller = new Seller(0.10, true);
        seller.addProductOffer(product, 80);
        model.addSeller(seller);

        Buyer buyer = new Buyer(150.0, 15.0, 0.6);
        buyer.addNeed(product, 4);
        model.addBuyer(buyer);

        while (!model.getCentralBank().checkStability() && model.getCurrentTurn() < 100) {
            model.nextTurn();
        }

        assertTrue(model.getCentralBank().checkStability());

        seller.setProfitMargin(0.8);
        System.out.println("\n=== ZWIĘKSZONO MARŻĘ SPRZEDAWCY DO 80% ===");

        for (int i = 0; i < 100; i++) {
            model.nextTurn();
        }

        assertTrue(model.getCentralBank().checkStability());
        displayAndWaitForChart(createInflationChart(model, "Zmiana marży"), "Test 3");
    }

    @Test
    void testBuyerIncomeDropShock() throws InterruptedException {
        MarketModel model = new MarketModel(0.04, true);

        Product product = new Product("Makaron", Product.Type.NECESSITY, 2.0, 7);
        Seller seller = new Seller(0.10, true);
        seller.addProductOffer(product, 80);
        model.addSeller(seller);

        Buyer buyer = new Buyer(250.0, 25.0, 0.6);
        buyer.addNeed(product, 3);
        model.addBuyer(buyer);

        while (!model.getCentralBank().checkStability() && model.getCurrentTurn() < 100) {
            model.nextTurn();
        }

        buyer.adjustIncome(0.1);
        System.out.println("\n=== SZOK: SPADEK DOCHODU KUPUJĄCEGO O 50% ===");

        for (int i = 0; i < 50; i++) {
            model.nextTurn();
        }

        assertTrue(model.getCentralBank().checkStability());
        displayAndWaitForChart(createInflationChart(model, "Spadek dochodu"), "Test 7");
    }

    //niepop
    @Test
    void testNewSellerEntersMarket() throws InterruptedException {
        MarketModel model = new MarketModel(0.05, true);

        Product product = new Product("Ser", Product.Type.NECESSITY, 6.0, 4);
        Seller seller1 = new Seller(0.15, true);
        seller1.addProductOffer(product, 30);
        model.addSeller(seller1);

        Buyer buyer = new Buyer(200.0, 20.0, 0.6);
        buyer.addNeed(product, 2);
        model.addBuyer(buyer);

        while (!model.getCentralBank().checkStability() && model.getCurrentTurn() < 100) {
            model.nextTurn();
        }

        Seller seller2 = new Seller(0.08, true);
        seller2.addProductOffer(product, 50);
        model.addSeller(seller2);
        System.out.println("\n=== NOWY SPRZEDAWCA WSZEDŁ NA RYNEK ===");

        for (int i = 0; i < 40; i++) {
            model.nextTurn();
        }

        assertTrue(model.getCentralBank().checkStability());
        displayAndWaitForChart(createInflationChart(model, "Nowy sprzedawca"), "Test 8");
    }

    @Test
    void testNewBuyerWithHighDemand() throws InterruptedException {
        MarketModel model = new MarketModel(0.05, true);

        Product product = new Product("Czekolada", Product.Type.LUXURY, 8.0, 2);
        Seller seller = new Seller(0.20, true);
        seller.addProductOffer(product, 50);
        model.addSeller(seller);

        Buyer buyer1 = new Buyer(150.0, 15.0, 0.5);
        buyer1.addNeed(product, 1);
        model.addBuyer(buyer1);

        while (!model.getCentralBank().checkStability() && model.getCurrentTurn() < 100) {
            model.nextTurn();
        }

        Buyer buyer2 = new Buyer(400.0, 40.0, 0.8);
        buyer2.addNeed(product, 3);
        model.addBuyer(buyer2);
        System.out.println("\n=== NOWY KUPUJĄCY Z DUŻYM POPYTEM ===");

        for (int i = 0; i < 40; i++) {
            model.nextTurn();
        }

        assertTrue(model.getCentralBank().checkStability());
        displayAndWaitForChart(createInflationChart(model, "Nowy kupujący"), "Test 9");
    }
}

