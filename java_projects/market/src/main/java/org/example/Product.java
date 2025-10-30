package org.example;

class Product {
    public enum Type { NECESSITY, LUXURY }

    private final String name;
    private final Type type;
    private final double productionCost;
    private final int baseDemand;

    public Product(String name, Type type, double productionCost, int baseDemand) {
        this.name = name;
        this.type = type;
        this.productionCost = productionCost;
        this.baseDemand = baseDemand;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public double getProductionCost() {
        return productionCost;
    }

    public int getBaseDemand() {
        return baseDemand;
    }

//    public void setProductionCost(double productionCost) {
//        this.productionCost = productionCost;
//    }

    @Override
    public String toString() {
        return name + " (" + type + ", koszt: " + productionCost + ")";
    }
}
