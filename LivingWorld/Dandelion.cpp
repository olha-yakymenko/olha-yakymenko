
#include "Dandelion.h"
#include "World.h"
#include <iostream>
#include "Sheep.h"

extern const int CELL_SIZE;

Dandelion::Dandelion(int power, Position position, World* world)
    : Plant(0, position, world) {
        initializeAttributes();  // Wywołanie funkcji pomocniczej
}

Dandelion::Dandelion(int power, Position position, std::string species, World* world)
    : Plant(0, position, world) {
    setSpecies(species);  // Ustawienie konkretnego gatunku
    initializeAttributes();  // Wywołanie funkcji pomocniczej
}

Dandelion::Dandelion()
    : Plant(0, Position(0, 0), nullptr) {
        initializeAttributes();  // Wywołanie funkcji pomocniczej
}
void Dandelion::initializeAttributes() {
    setSpecies("Dandelion");
    setInitiative(0);
    setLiveLength(6);
    setPowerToReproduce(2);
    setSign('D');
}


void Dandelion::spread() {
    if (this->getLiveLength() <= 0) return;

    if (getWorld() == nullptr) {
        std::cerr << "World is not initialized!" << std::endl;
        return;
    }

    // Lista możliwych pozycji, na które trawa może się rozprzestrzenić
    std::vector<Position> adjacentPositions = {
        Position(getPosition().getX() - 1, getPosition().getY()), // Lewa
        Position(getPosition().getX() + 1, getPosition().getY()), // Prawa
        Position(getPosition().getX(), getPosition().getY() - 1), // Górna
        Position(getPosition().getX(), getPosition().getY() + 1)  // Dolna
    };

    // Iteracja po sąsiednich pozycjach
    for (auto& adjacentPos : adjacentPositions) {
        // Sprawdź, czy sąsiednia pozycja jest w obrębie planszy
        if (adjacentPos.isValid()) {
            // Sprawdź, czy na tej pozycji nie ma innego organizmu
            Organism* organismAtPos = getWorld()->getOrganismFromPosition(adjacentPos);

            if (organismAtPos == nullptr) {
                Dandelion* newDandelion = new Dandelion(3, adjacentPos, getWorld());
                newDandelion->setBirthTurn(getWorld()->getCurrentTurn()); // Ustawienie tury narodzin
                newDandelion->setAncestorsHistory(this->getAncestorsHistory());
                newDandelion->addAncestor(getWorld()->getCurrentTurn(), -1);

                getWorld()->addOrganism(newDandelion);
                std::cout << "Dandelion spread to position: " << adjacentPos.toString() << std::endl;
                setPower(getPower() / 2);
                break;  // Tylko jedna trawa rozprzestrzenia się w tej turze
            } 
        }
    }
}

Dandelion* Dandelion::clone() const {
   return new Dandelion(*this);
}

std::string Dandelion::toString() const {
    return "Dandelion at " + getPosition().toString();
}



void Dandelion::collision(Organism* other) {
    if (getLiveLength() <= 0) return;

    // Sprawdzamy, czy "other" jest obiektem typu "Sheep"
    Sheep* sheep = dynamic_cast<Sheep*>(other);  // Próba rzutowania na typ "Sheep"
    
    if (sheep != nullptr) {
        // Jeśli rzutowanie się udało, to znaczy, że "other" to obiekt typu "Sheep"
        this->setLiveLength(0);  // Dandelion umiera
        int currentTurn = world->getCurrentTurn();  // Użyj operatora '->', bo 'world' jest wskaźnikiem

        setDeathTurn(currentTurn);
    }
    // Jeśli to nie jest owca, nie robimy nic
}

Dandelion::Dandelion(const Dandelion& other)
    : Plant(other) {
        initializeAttributes();
}

Dandelion::Dandelion(Dandelion&& other) noexcept
    : Plant(std::move(other)) {
        initializeAttributes();
}

Dandelion& Dandelion::operator=(const Dandelion& other) {
    if (this != &other) {
        Plant::operator=(other);
        initializeAttributes();
    }
    return *this;
}

Dandelion& Dandelion::operator=(Dandelion&& other) noexcept {
    if (this != &other) {
        Plant::operator=(std::move(other));
        initializeAttributes();
    }
    return *this;
}

Dandelion::~Dandelion() {
    // Jeśli dodasz pola wymagające zwalniania pamięci – zadbaj o to tutaj
}


void Dandelion::draw(SDL_Renderer* renderer) {
    Position pos = getPosition();
    SDL_Rect rect = {pos.getX() * CELL_SIZE, pos.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE};

    SDL_SetRenderDrawColor(renderer, 255, 255, 0, 255); // Żółty
    SDL_RenderFillRect(renderer, &rect);

    SDL_SetRenderDrawColor(renderer, 50, 50, 50, 255);
    SDL_RenderDrawRect(renderer, &rect);
}
