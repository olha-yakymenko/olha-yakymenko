package org.example;

import java.util.*;
import java.util.stream.Collectors;

class Buyer implements Observer, Observable {
    private final Map<Product, Integer> needs = new HashMap<>();
    private double budget;
    private final double incomePerTurn;
    private final List<String> purchaseHistory = new ArrayList<>();
    private final int id;
    private static int nextId = 1;
    public double priceSensitivity;
    private final List<Seller> knownSellers = new ArrayList<>();
    public double currentInflation;
    public final Map<Product, Double> priceHistory = new HashMap<>();
    private final Random random = new Random();

    private final List<Observer> observers = new ArrayList<>();

    private final Map<Product, Integer> purchasedQuantities = new HashMap<>();



    // Progi cenowe dla reakcji
    private static final double PRICE_DROP_THRESHOLD = 0.95; // 5% spadek ceny
    private static final double PRICE_INCREASE_THRESHOLD = 1.05; // 5% wzrost ceny
    private static final double LUXURY_BUDGET_LIMIT = 0.3; // Max 30% budżetu na luksus

    public Buyer(double initialBudget, double incomePerTurn, double priceSensitivity) {
        this.budget = initialBudget;
        this.incomePerTurn = incomePerTurn;
        this.priceSensitivity = priceSensitivity;
        this.id = nextId++;
    }

    public void addNeed(Product product, int priority) {
        needs.put(product, priority);
        priceHistory.put(product, Double.MAX_VALUE); // Inicjalizacja śledzenia ceny
        System.out.println("Kupujący " + id + " dodał potrzebę: " + product.getName() +
                " (priorytet: " + priority + ")");
    }

    public void observeSellers(List<Seller> sellers) {
        this.knownSellers.clear();
        this.knownSellers.addAll(sellers);
        // Rejestracja jako obserwator u wszystkich sprzedawców
        sellers.forEach(s -> s.addObserver(this));
    }



    @Override
    public void update(ProductOffer offer) {
        Product product = offer.getProduct();
        double newPrice = offer.getPrice();
        maybeUpdateNeedsFromMarket();

        if (!priceHistory.containsKey(product)) {
            priceHistory.put(product, newPrice);
            System.out.println("Kupujący " + id + " zapisuje pierwszą cenę " + newPrice + " dla " + product.getName());
            return; // pierwsza cena, nie kupujemy
        }

        double oldPrice = priceHistory.get(product);
        priceHistory.put(product, newPrice);

        double priceChangeRatio = newPrice / oldPrice;

        if (priceChangeRatio < PRICE_DROP_THRESHOLD) {
            // Duży spadek - kupuj zawsze
            System.out.println("Kupujący " + id + " zauważył duży spadek ceny " +
                    product.getName() + " z " + oldPrice + " na " + newPrice);
            considerPurchase(product);
        } else if (priceChangeRatio < 1.0) {
            // Mały spadek (mniejszy niż 5%), kupuj losowo
            System.out.println("Kupujący " + id + " zauważył mały spadek ceny " +
                    product.getName() + " z " + oldPrice + " na " + newPrice + " - decyzja losowa");

            if (random.nextBoolean()) { // 50% szans na zakup
                considerPurchase(product);
            } else {
                System.out.println("Kupujący " + id + " zdecydował się nie kupować przy małym spadku ceny.");
            }
        } else if (priceChangeRatio > PRICE_INCREASE_THRESHOLD) {
            System.out.println("Kupujący " + id + " zauważył wzrost ceny " +
                    product.getName() + " z " + oldPrice + " na " + newPrice);
        }
    }


    public void update(double inflation) {
        this.currentInflation = inflation;
        System.out.println("Kupujący " + id + " zaktualizował inflację na: " +
                String.format("%.2f", inflation * 100) + "%");
    }

    public void makePurchaseDecisions() {
//        budget += incomePerTurn;
        adjustIncome(incomePerTurn);
        System.out.println("\nKupujący " + id + " rozpoczyna turę z budżetem: " +
                String.format("%.2f", budget));

        // Najpierw produkty pierwszej potrzeby
        purchaseByType(Product.Type.NECESSITY);

        // Następnie produkty luksusowe (jeśli zostanie budżet)
        purchaseByType(Product.Type.LUXURY);


    }

    private void purchaseByType(Product.Type type) {
        List<Map.Entry<Product, Integer>> productsToConsider = needs.entrySet().stream()
                .filter(entry -> entry.getKey().getType() == type)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        for (Map.Entry<Product, Integer> entry : productsToConsider) {
            Product product = entry.getKey();
            int priority = entry.getValue();

            Optional<ProductOffer> bestOffer = findBestOffer(product);

            bestOffer.ifPresent(offer -> {
                int quantity = calculateQuantityToBuy(product, offer, priority);
                if (quantity > 0) {
                    executePurchase(product, quantity, offer);
                }
            });
        }
    }

    private Optional<ProductOffer> findBestOffer(Product product) {
        return knownSellers.stream()
                .flatMap(seller -> seller.getOffers().stream())
                .filter(offer -> offer.getProduct().equals(product))
                .filter(offer -> offer.getAvailableQuantity() > 0)
                .min(Comparator.comparingDouble(ProductOffer::getPrice));
    }

    private int calculateQuantityToBuy(Product product, ProductOffer offer, int priority) {
        double price = offer.getPrice();
        int available = offer.getAvailableQuantity();
        int maxAffordable = (int) (budget / price);

        if (product.getType() == Product.Type.NECESSITY) {
            // Dla produktów pierwszej potrzeby kupujemy nawet przy wyższych cenach
            double priceImpact = priceSensitivity * price / 100;
            int desired = (int) (priority / (1 + priceImpact));
            return Math.max(1, Math.min(desired, Math.min(maxAffordable, available)));
        }
        else { // LUXURY
            // Dla luksusów sprawdzamy czy cena nie przekracza limitu
            if (price > budget * LUXURY_BUDGET_LIMIT) {
                System.out.println("Cena " + product.getName() + " (" + price +
                        ") przekracza limit luksusowy (" +
                        String.format("%.2f", budget * LUXURY_BUDGET_LIMIT) + ")");
                return 0;
            }
            return Math.min(priority, Math.min(maxAffordable, available));
        }
    }


    private void executePurchase(Product product, int quantity, ProductOffer offer) {
        double cost = quantity * offer.getPrice();

        // Zabezpieczenie przed zejściem poniżej zera
        if (budget < cost) {
            System.out.println("Kupujący " + id + " nie ma wystarczającego budżetu na zakup " +
                    quantity + "x " + product.getName() + " (koszt: " +
                    String.format("%.2f", cost) + ", budżet: " + String.format("%.2f", budget) + ")");
            return;
        }

        for (Seller seller : knownSellers) {
            if (seller.getOffers().contains(offer)) {
                if (seller.sellProduct(product, quantity, this)) {
                    budget -= cost;
                    String log = "Kupujący " + id + " kupił " + quantity + "x " +
                            product.getName() + " za " + String.format("%.2f", cost) +
                            " (pozostały budżet: " + String.format("%.2f", budget) + ")";
                    purchaseHistory.add(log);
                    System.out.println(log);

                    purchasedQuantities.put(product,
                            purchasedQuantities.getOrDefault(product, 0) + quantity);

                    break;
                }
            }
        }

    }
    public boolean hasSatisfiedNeeds() {
        for (Map.Entry<Product, Integer> need : needs.entrySet()) {
            Product product = need.getKey();
            int priority = need.getValue();
            int purchased = purchasedQuantities.getOrDefault(product, 0);
            if (purchased < priority) {
                return false;
            }
        }
        return true;
    }

    private void maybeUpdateNeedsFromMarket() {
        for (Seller seller : knownSellers) {
            for (ProductOffer offer : seller.getOffers()) {
                Product product = offer.getProduct();
                if (needs.containsKey(product)) {
                    if (product.getType() == Product.Type.NECESSITY) {
                        int currentPriority = needs.get(product);
                        int increment = random.nextInt(5) + 1; // losowe zwiększenie 1–5
                        int newPriority = currentPriority + increment;
                        updateNeed(product, newPriority);
                    }
                } else {
                    if (random.nextDouble() < 0.1) { // 10% szans na dodanie nowej potrzeby
                        int priority = random.nextInt(10) + 1;
                        addNeed(product, priority);
                    }
                }
            }
        }
    }



    private void considerPurchase(Product product) {
        if (!needs.containsKey(product)) return;

        Optional<ProductOffer> bestOffer = findBestOffer(product);
        bestOffer.ifPresent(offer -> {
            int priority = needs.get(product);
            int quantity = calculateQuantityToBuy(product, offer, priority);
            if (quantity > 0) {
                executePurchase(product, quantity, offer);
            }
        });
    }

    public void adjustIncome(double liczba){
        this.budget=budget+liczba;
        System.out.println("BUDzet akt");
        notifyObservers();
    } // Zwiększenie dochodu o 50%

    // Metody pomocnicze
    public double getBudget() { return budget; }
    public Map<Product, Integer> getNeeds() { return Collections.unmodifiableMap(needs); }
    public List<String> getPurchaseHistory() { return Collections.unmodifiableList(purchaseHistory); }

    @Override
    public String toString() {
        return "Kupujący " + id + " (budżet: " + String.format("%.2f", budget) + ")";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    // Nowa metoda do dostosowywania zachowania
    public void adjustPriceSensitivity(double inflationImpact) {
        this.priceSensitivity *= (1 + inflationImpact);
        System.out.println("Kupujący " + id + " dostosował wrażliwość na ceny do: "
                + String.format("%.2f", priceSensitivity));
    }


    public void updateNeed(Product product, int newPriority) {
        if (needs.containsKey(product)) {
            System.out.println("Kupujący " + id + " aktualizuje potrzebę " + product.getName() +
                    " z priorytetu " + needs.get(product) + " na " + newPriority);
            needs.put(product, newPriority);
        } else {
            // Jeśli potrzeba jeszcze nie istnieje, można ją dodać
            System.out.println("Kupujący " + id + " dodaje nową potrzebę " + product.getName() +
                    " z priorytetem " + newPriority);
            needs.put(product, newPriority);
            priceHistory.put(product, Double.MAX_VALUE); // Inicjalizacja śledzenia ceny
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        System.out.println("Inf o budzecie " + budget);
        observers.forEach(o -> o.update(budget));
    }

    public void update(Observable a, double b){

    }


}





