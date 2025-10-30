//package org.example;
//
//import org.knowm.xchart.style.Styler;
//
//import org.knowm.xchart.XYChart;
//import org.knowm.xchart.XYChartBuilder;
//import org.knowm.xchart.SwingWrapper;
//import org.knowm.xchart.style.Styler;
//
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class InflationPlotter {
//
//    public static void plotInflation(List<Double> inflationHistoryPercent) {
//        List<Integer> turns = IntStream.rangeClosed(1, inflationHistoryPercent.size())
//                .boxed()
//                .collect(Collectors.toList());
//
//        XYChart chart = new XYChartBuilder()
//                .width(800)
//                .height(600)
//                .title("Inflacja w czasie")
//                .xAxisTitle("Tura")
//                .yAxisTitle("Inflacja [%]")
//                .build();
//
//        chart.addSeries("Inflacja", turns, inflationHistoryPercent);
//
//        new SwingWrapper<>(chart).displayChart();
//    }
//
//    public static void plotFullAnalysis(List<Double> inflationHistory,
//                                        List<Double> revenueHistory,
//                                        double targetRevenue) {
//        // Przygotuj dane
//        List<Integer> turns = IntStream.range(0, inflationHistory.size())
//                .boxed()
//                .collect(Collectors.toList());
//
//        List<Double> targetLine = turns.stream()
//                .map(t -> targetRevenue)
//                .collect(Collectors.toList());
//
//        // Stwórz wykres kombinowany
//        XYChart chart = new XYChartBuilder()
//                .width(1000)
//                .height(600)
//                .title("Analiza stabilności systemu")
//                .xAxisTitle("Tura")
//                .build();
//
//        // Dodaj serie
//        chart.addSeries("Inflacja (%)", turns, inflationHistory)
//                .setYAxisGroup(0);
//        chart.addSeries("Wpływy podatkowe", turns, revenueHistory)
//                .setYAxisGroup(1);
//        chart.addSeries("Cel podatkowy", turns, targetLine)
//                .setYAxisGroup(1);
//
//        // Konfiguracja osi
//        chart.getStyler().setYAxisGroupTitle(0, "Inflacja [%]");
//        chart.getStyler().setYAxisGroupTitle(1, "Wpływy");
//        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
//
//        new SwingWrapper<>(chart).displayChart();
//    }
//}
//




package org.example;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InflationPlotter {

    public static void plotInflation(List<Double> inflationHistoryPercent) {
        List<Integer> turns = IntStream.rangeClosed(1, inflationHistoryPercent.size())
                .boxed()
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Inflacja w czasie")
                .xAxisTitle("Tura")
                .yAxisTitle("Inflacja [%]")
                .build();

        chart.addSeries("Inflacja", turns, inflationHistoryPercent);

        new SwingWrapper<>(chart).displayChart();
    }

    public static void plotFullAnalysis(List<Double> inflationHistory,
                                        List<Double> revenueHistory,
                                        double targetRevenue) {
        // Przygotuj dane
        List<Integer> turns = IntStream.range(0, inflationHistory.size())
                .boxed()
                .collect(Collectors.toList());

        List<Double> targetLine = turns.stream()
                .map(t -> targetRevenue)
                .collect(Collectors.toList());

        // Stwórz wykres
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(600)
                .title("Analiza stabilności systemu")
                .xAxisTitle("Tura")
                .yAxisTitle("Wartości [% / zł]")  // Jeden ogólny tytuł osi Y
                .build();

        // Dodaj serie
        chart.addSeries("Inflacja [%]", turns, inflationHistory);
        chart.addSeries("Wpływy podatkowe [zł]", turns, revenueHistory);
        chart.addSeries("Cel podatkowy [zł]", turns, targetLine);

        // Ustawienia stylu
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setMarkerSize(6);

        new SwingWrapper<>(chart).displayChart();
    }
}
