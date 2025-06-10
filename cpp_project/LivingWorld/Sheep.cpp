#include "Sheep.h"
#include "World.h"
#include "Grass.h"
#include <iostream>
#include <cstdlib>

extern const int CELL_SIZE;

void Sheep::initializeAttributes() {
    setSpecies("Sheep");
    setInitiative(3);
    setLiveLength(10);
    setPowerToReproduce(6);
    setSign('S');
}


Sheep::Sheep(Position position, World* world)
    : Animal(3, position, world) {
    initializeAttributes();
}

Sheep::Sheep(int power, Position position, std::string species, World* world)
    : Animal(power, position, world) {
    initializeAttributes();
}

Sheep::Sheep()
    : Animal(3, Position(0, 0), nullptr) {
    initializeAttributes();
}


Animal* Sheep::clone() const {
    return new Sheep(*this);  // Implementacja w pliku źródłowym
}


std::string Sheep::toString() const {
    return "Sheep at (" + std::to_string(getPosition().getX()) + ", " + std::to_string(getPosition().getY()) + ")";
}


void Sheep::collision(Organism* other) {
    if (getLiveLength() <= 0) return;

    if (other == nullptr) return;

    std::string species = other->getSpecies();

    if (species == "Grass") {
        std::cout << "Sheep at " << getPosition().toString() << " eats grass!" << std::endl;
        setPower(getPower() + other->getPower());
    } else if (species == "Toadstool") {
        std::cout << "Sheep at " << getPosition().toString() << " eats poisonous mushroom and dies!" << std::endl;
        setLiveLength(0);  // Owca umiera
        int currentTurn = world->getCurrentTurn();  // Użyj operatora '->', bo 'world' jest wskaźnikiem

        setDeathTurn(currentTurn);
    } else if (species == "Wolf") {
        std::cout << "Sheep at " << getPosition().toString() << " was eaten by a wolf!" << std::endl;
        setLiveLength(0);  // Owca umiera
        int currentTurn = world->getCurrentTurn();  // Użyj operatora '->', bo 'world' jest wskaźnikiem

        setDeathTurn(currentTurn);
    } else if (species == "Dandelion") {
        std::cout << "Sheep at " << getPosition().toString() << " was eaten a dandelion!" << std::endl;
        setLiveLength(getLiveLength()+1);  
    } 
    
}


void Sheep::move(int dx, int dy) {
    if (getLiveLength() <= 0) return;

    // Sprawdzamy otoczenie w poszukiwaniu trawy
    Position currentPos = getPosition();
    std::pair<int, int> bestMove = findBestMove();  // Znajdź najlepszy ruch (w stronę trawy)

    // Jeśli nie ma trawy, poruszamy się losowo
    if (bestMove == std::pair<int, int>{0, 0}) {
        // Losowy ruch (w 4 kierunkach)
        bestMove = {rand() % 3 - 1, rand() % 3 - 1};  // Losowy kierunek: {-1, 0, 1} dla x i y
    }

    Position newPos = currentPos;
    newPos.move(bestMove.first, bestMove.second);  // Ruch do najlepszego pola

    if (world != nullptr) {
        Organism* target = world->getOrganismFromPosition(newPos);
        collision(target);  // Kolizja (np. owca może zjeść trawę, jeśli to będzie możliwe)
        setPosition(newPos);  // Owca przemieszcza się
    } else {
        setPosition(newPos);  // Jeśli nie ma świata, po prostu się przemieszcza
    }
}

// std::pair<int, int> Sheep::findBestMove() {
//     // Kierunki: góra, prawo, dół, lewo
//     std::vector<std::pair<int, int>> directions = {
//         {0, 1}, {1, 0}, {0, -1}, {-1, 0}
//     };

//     Position current = getPosition();
//     std::pair<int, int> bestMove = {0, 0};  // Domyślnie nie ruszaj się
//     int bestPriority = 0;

//     // Sprawdzamy, czy world jest poprawnie ustawiony
//     if (world == nullptr) {
//         std::cerr << "Error: world is null!" << std::endl;
//         return bestMove;  // Zwracamy domyślny ruch, jeśli world jest null
//     }

//     for (auto [dx, dy] : directions) {
//         Position checkPos = current;
//         checkPos.move(dx, dy);
//         Organism* target = world->getOrganismFromPosition(checkPos);

//         int priority = 0;

//         // Jeśli na sprawdzanej pozycji jest trawa, nadamy wyższy priorytet
//         if (dynamic_cast<Grass*>(target)) {  
//             priority = 2;  // Wysoki priorytet dla trawy
//         } 
//         // Jeśli nie ma organizmu (puste pole), nadamy średni priorytet
//         else if (target == nullptr) {  
//             priority = 1;  // Puste pole ma średni priorytet
//         }

//         // Jeśli znaleziono lepszy priorytet, zapisz ten kierunek jako najlepszy
//         if (priority > bestPriority) {
//             bestPriority = priority;
//             bestMove = {dx, dy};
//         }
//     }

//     return bestMove;
// }


std::pair<int, int> Sheep::findBestMove() {
    Position current = getPosition();
    std::pair<int, int> bestMove = {0, 0};  // Domyślnie nie ruszaj się
    int bestPriority = 0;
    int maxDistance = 5;

    if (world == nullptr) {
        std::cerr << "Error: world is null!" << std::endl;
        return bestMove;
    }

    for (int dx = -maxDistance; dx <= maxDistance; ++dx) {
        for (int dy = -maxDistance; dy <= maxDistance; ++dy) {
            if (dx == 0 && dy == 0)
                continue;  // pomiń aktualną pozycję

            Position checkPos = current;
            checkPos.move(dx, dy);

            if (!world->isPositionFree(checkPos))
                continue;

            Organism* target = world->getOrganismFromPosition(checkPos);
            int priority = 0;

            if (dynamic_cast<Grass*>(target)) {
                priority = 2;  // Najlepszy cel: trawa
            } else if (target == nullptr) {
                priority = 1;  // Puste pole – może iść
            }

            if (priority > bestPriority) {
                bestPriority = priority;

                // Oblicz kierunek jako pierwszy krok w stronę celu
                int stepX = (dx != 0) ? dx / std::abs(dx) : 0;
                int stepY = (dy != 0) ? dy / std::abs(dy) : 0;

                bestMove = {stepX, stepY};
            }
        }
    }

    return bestMove;
}


Animal* Sheep::createOffspring(Position pos) {
    return new Sheep(pos, world);  // zakładając że masz taki konstruktor
}


Sheep& Sheep::operator=(const Sheep& other) {
    if (this != &other) {  // Sprawdzenie, czy nie przypisujemy do samego siebie
        Animal::operator=(other);  // Kopiowanie danych z klasy bazowej
        ancestorsHistory = other.ancestorsHistory;  // Kopiowanie historii przodków
        setSpecies(other.getSpecies());
        setInitiative(other.getInitiative());
        setLiveLength(other.getLiveLength());
        setPowerToReproduce(other.getPowerToReproduce());
        setSign(other.getSign());
    }
    return *this;
}

// Operator przypisania przenoszącego
Sheep& Sheep::operator=(Sheep&& other) noexcept {
    if (this != &other) {  // Sprawdzenie, czy nie przypisujemy do samego siebie
        Animal::operator=(std::move(other));  // Przenoszenie danych z klasy bazowej
        ancestorsHistory = std::move(other.ancestorsHistory);  // Przenoszenie historii przodków
        setSpecies(std::move(other.getSpecies()));
        setInitiative(other.getInitiative());
        setLiveLength(other.getLiveLength());
        setPowerToReproduce(other.getPowerToReproduce());
        setSign(other.getSign());
    }
    return *this;
}

Sheep::~Sheep() {
    // Jeśli nie masz dynamicznych wskaźników — pusty wystarczy
}

Sheep::Sheep(const Sheep& other)
    : Animal(other) {
    ancestorsHistory = other.ancestorsHistory;
    setSpecies(other.getSpecies());
    setInitiative(other.getInitiative());
    setLiveLength(other.getLiveLength());
    setPowerToReproduce(other.getPowerToReproduce());
    setSign(other.getSign());
}

Sheep::Sheep(Sheep&& other) noexcept
    : Animal(std::move(other)) {
    ancestorsHistory = std::move(other.ancestorsHistory);
    setSpecies(std::move(other.getSpecies()));
    setInitiative(other.getInitiative());
    setLiveLength(other.getLiveLength());
    setPowerToReproduce(other.getPowerToReproduce());
    setSign(other.getSign());
}


void Sheep::draw(SDL_Renderer* renderer) {
    Position pos = getPosition();
    SDL_Rect rect = {pos.getX() * CELL_SIZE, pos.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE};

    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255); // Biały
    SDL_RenderFillRect(renderer, &rect);

    SDL_SetRenderDrawColor(renderer, 50, 50, 50, 255);
    SDL_RenderDrawRect(renderer, &rect);
}
