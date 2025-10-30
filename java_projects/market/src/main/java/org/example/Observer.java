package org.example;

interface Observer {
    void update(ProductOffer offer);
    void update(double inflation);
    void update(Observable o, double l);

}
