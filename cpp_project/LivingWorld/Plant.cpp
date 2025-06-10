#include "Plant.h"

Plant::Plant(int power, Position position, World* world)
    : Organism(power, position, "Plant", 0, 0, 1, 'P', world) {
}

// Konstruktor domyślny
Plant::Plant() : Organism(0, Position(0, 0), "Plant", 0, 0, 1, 'P', nullptr) {
}

void Plant::move(int dx, int dy) {

}


void Plant::spread() {
    // Logika rozprzestrzeniania dla rośliny
}

Organism* Plant::clone() const {
    return new Plant(*this);  // Tworzenie kopii rośliny
}

std::string Plant::toString() const {
    return species + " at (" + std::to_string(position.getX()) + ", " + std::to_string(position.getY()) + ")";
}


void Plant::collision(Organism* other) {
}

