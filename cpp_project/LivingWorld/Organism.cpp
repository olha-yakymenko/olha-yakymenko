#include "Organism.h"
#include "World.h"

Organism::Organism() 
    : power(0), initiative(0), liveLength(0), powerToReproduce(0), 
      initialPowerToReproduce(0), sign(' '), world(nullptr) {}

      Organism::Organism(int power, Position position, std::string species,
        int initiative, int liveLength, int powerToReproduce,
        char sign, World* world)
        : power(power), position(position), species(species),
        initiative(initiative), liveLength(liveLength),
        powerToReproduce(powerToReproduce), initialPowerToReproduce(powerToReproduce),
        sign(sign), world(world) {
            // addAncestor(birthTurn, 0);
            if (world) {
                birthTurn = world->getCurrentTurn();  
            } else {
                birthTurn = 0;  
            }
        
            addAncestor(birthTurn, 0); 
        }




Organism::Organism(const Organism& other)
     : power(other.power), position(other.position), species(other.species),
       initiative(other.initiative), liveLength(other.liveLength),
       powerToReproduce(other.powerToReproduce), initialPowerToReproduce(other.initialPowerToReproduce),
       sign(other.sign), world(other.world), ancestorsHistory(other.ancestorsHistory) {}
 
 Organism::Organism(Organism&& other) noexcept
     : power(other.power), position(std::move(other.position)), species(std::move(other.species)),
       initiative(other.initiative), liveLength(other.liveLength),
       powerToReproduce(other.powerToReproduce), initialPowerToReproduce(other.initialPowerToReproduce),
       sign(other.sign), world(other.world), ancestorsHistory(std::move(other.ancestorsHistory)) {}  // lub std::exchange(other.birthTurn, -1)
 
int Organism::getPower() const {
    return power;
}

void Organism::setPower(int power) {
    this->power = power;
}

Position Organism::getPosition() const {
    return position;
}

// void Organism::setPosition(Position position) {
//     this->position = position;
// }

void Organism::setPosition(Position newPosition) {
    lastPosition = position;  // zapisz starą pozycję
    position = newPosition;   // ustaw nową
}


std::string Organism::getSpecies() const {
    return species;
}

void Organism::setSpecies(const std::string& species) {
    this->species = species;
}

int Organism::getInitiative() const {
    return initiative;
}

void Organism::setInitiative(int initiative) {
    this->initiative = initiative;
}

int Organism::getLiveLength() const {
    return liveLength;
}

void Organism::setLiveLength(int liveLength) {
    this->liveLength = liveLength;
}

int Organism::getPowerToReproduce() const {
    return powerToReproduce;
}

void Organism::setPowerToReproduce(int powerToReproduce) {
    this->powerToReproduce = powerToReproduce;
}

char Organism::getSign() const {
    return sign;
}

void Organism::setSign(char sign) {
    this->sign = sign;
}

World* Organism::getWorld() const {
    return world;
}

void Organism::setWorld(World* world) {
    this->world = world;
}

bool Organism::isAlive() const {
    return liveLength > 0;
}

void Organism::increasePower() {
    power++;
}

void Organism::decreaseLife() {
    liveLength--;
}

void Organism::spread() {
    // Implementacja zależna od konkretnego organizmu
    // Może obejmować reprodukcję organizmu, np. sprawdzanie sąsiednich wolnych pozycji
}

std::string Organism::toString() const {
    return species + " at (" + std::to_string(position.getX()) + "," + std::to_string(position.getY()) + ")";
}



Organism& Organism::operator=(const Organism& other) {
    if (this != &other) {
        power = other.power;
        position = other.position;
        species = other.species;
        initiative = other.initiative;
        liveLength = other.liveLength;
        powerToReproduce = other.powerToReproduce;
        initialPowerToReproduce = other.initialPowerToReproduce;
        sign = other.sign;
        world = other.world;
        ancestorsHistory = other.ancestorsHistory; 
        birthTurn = other.birthTurn;
    }
    return *this;
}


Organism& Organism::operator=(Organism&& other) noexcept {
    if (this != &other) {
        power = other.power;
        position = std::move(other.position);
        species = std::move(other.species);
        initiative = other.initiative;
        liveLength = other.liveLength;
        powerToReproduce = other.powerToReproduce;
        initialPowerToReproduce = other.initialPowerToReproduce;
        sign = other.sign;
        world = other.world;
        ancestorsHistory = std::move(other.ancestorsHistory);  // Przenosimy historię przodków
        birthTurn = other.birthTurn;
        // Po przeniesieniu obiektu, można ustawić dane "inne" na wartości domyślne
        other.world = nullptr;
        other.power = 0;
        other.initiative = 0;
        other.liveLength = 0;
        other.powerToReproduce = 0;
        other.sign = ' ';
        other.birthTurn = 0;
    }
    return *this;
}



void Organism::addAncestor(int birthTurn, int deathTurn) {
    ancestorsHistory.emplace_back(birthTurn, deathTurn);
}

const std::vector<Ancestor>& Organism::getAncestorsHistory() const {
    return ancestorsHistory;
}
void Organism::setAncestorsHistory(const std::vector<Ancestor>& history) {
    ancestorsHistory = history;
}

void Organism::setDeathTurn(int deathTurn) {
    if (!ancestorsHistory.empty()) {
        ancestorsHistory.back().deathTurn = deathTurn;
    }
}



int Organism::getDeathTurn() const {
    return deathTurn;
}


void Organism::updateAncestorDeathTurn() {
    if (!ancestorsHistory.empty()) {
        ancestorsHistory.back().deathTurn = world->getCurrentTurn();
    }
}
