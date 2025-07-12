#include "Animal.h"
#include "World.h"
#include <iostream>
#include "Config.h"


extern const int CELL_SIZE; // Musi być zadeklarowana globalnie w Config.h

Animal::Animal(int power, Position position, World* world)
    : Organism(power, position, "Animal", 5, 10, 3, 'A', world), previousPosition(position) {}

Animal::Animal()
    : Organism(3, Position(0, 0), "Animal", 5, 10, 3, 'A', nullptr), previousPosition(Position(0, 0)) {}

void Animal::move(int dx, int dy) {

}

void Animal::spread() {
    // Można dodać logikę rozmnażania automatycznie
    // Lub zostawić pustą, zależnie od strategii gatunku
}

std::string Animal::toString() const {
    return species + " at (" + std::to_string(position.getX()) + ", " + std::to_string(position.getY()) + ")";
}


void Animal::reproduce(Animal* partner) {
    if (!partner || !world) return;

    if (this->getSpecies() != partner->getSpecies()) {
        return;  // Nie rozmnażają się, jeśli gatunki są różne
    }
    

    if (this->getPower() < this->getPowerToReproduce() ||
        partner->getPower() < partner->getPowerToReproduce()) {
        this->collision(partner); // domyślna reakcja
        return;
    }

    std::vector<Position> freePositions = world->getVectorOfFreePositionsAround(this->getPosition());
    if (freePositions.empty()) return;

    Position childPos = freePositions[rand() % freePositions.size()];
    Animal* child = this->createOffspring(childPos);  // używa wersji konkretnego gatunku
    child->setBirthTurn(world->getCurrentTurn());

    child->setAncestorsHistory(this->getAncestorsHistory());
    child->addAncestor(this->getBirthTurn(), this->getWorld()->getCurrentTurn());

    world->addOrganism(child);

    this->setPower(this->getPower() / 2);
    partner->setPower(partner->getPower() / 2);
}

void Animal::draw(SDL_Renderer* renderer) {

}
