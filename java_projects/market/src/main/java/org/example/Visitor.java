package org.example;


interface Visitor {
    void visit(Seller seller);
    void visit(Buyer buyer);
}

