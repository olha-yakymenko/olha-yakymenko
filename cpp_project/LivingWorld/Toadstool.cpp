
#include "Toadstool.h"
#include "World.h"
#include <iostream>

extern const int CELL_SIZE;

void Toadstool::initializeAttributes() {
    setSpecies("Toadstool");
    setInitiative(0);
    setLiveLength(12);
    setPowerToReproduce(4);
    setSign('T');
}

Toadstool::Toadstool(int power, Position position, World* world)
    : Plant(power, position, world) {
        initializeAttributes();
}

Toadstool::Toadstool(int power, Position position, std::string species, World* world)
    : Plant(power, position, world) {
        initializeAttributes();
}

Toadstool::Toadstool()
    : Plant(0, Position(0, 0), nullptr) {
        initializeAttributes();
}


void Toadstool::spread() {
    if (getLiveLength() <= 0) return;

    // Ustal, czy grzyb może się rozmnożyć, korzystając z metody getPowerToReproduce
    int powerToReproduce = this->getPowerToReproduce();  // Odczytanie granicy siły potrzebnej do rozmnożenia

    // Jeśli siła organizmu jest większa lub równa niż wymagane minimum, może się rozmnożyć
    if (getPower() >= powerToReproduce) {
        if (getWorld() == nullptr) {
            std::cerr << "Toadstool: World is not initialized!" << std::endl;
            return;
        }

        // Lista możliwych pozycji, na które grzyb może się rozprzestrzenić
        std::vector<Position> adjacentPositions = {
            Position(getPosition().getX() - 1, getPosition().getY()), // Lewa
            Position(getPosition().getX() + 1, getPosition().getY()), // Prawa
            Position(getPosition().getX(), getPosition().getY() - 1), // Górna
            Position(getPosition().getX(), getPosition().getY() + 1)  // Dolna
        };

        // Szukamy pierwszej wolnej komórki wśród sąsiednich pozycji
        for (auto& adjacentPos : adjacentPositions) {
            // Sprawdź, czy sąsiednia pozycja jest w obrębie planszy
            if (adjacentPos.isValid()) {
                // Sprawdź, czy na tej pozycji nie ma innego organizmu
                Organism* organismAtPos = getWorld()->getOrganismFromPosition(adjacentPos);

                if (organismAtPos == nullptr) {
                    // Jeśli pozycja jest wolna, rozprzestrzenić grzyba
                    Toadstool* newToadstool = new Toadstool(3, adjacentPos, getWorld());
                    newToadstool->setBirthTurn(getWorld()->getCurrentTurn());  // Ustawienie tury narodzin
                    newToadstool->setAncestorsHistory(this->getAncestorsHistory());
                    newToadstool->addAncestor(getWorld()->getCurrentTurn(), -1);
                    getWorld()->addOrganism(newToadstool);
                    std::cout << "Toadstool spread to position: " << adjacentPos.toString() << std::endl;

                    // Po rozmnożeniu grzyb traci połowę swojej siły
                    this->setPower(this->getPower() / 2);  // Traci połowę siły

                    break;  // Tylko jeden grzyb rozprzestrzenia się w tej turze
                }
            }
        }
    }
}


Toadstool* Toadstool::clone() const {
    return new Toadstool(*this);
}

std::string Toadstool::toString() const {
    return "Toadstool at " + getPosition().toString();
}

void Toadstool::collision(Organism* attacker) {
    if (getLiveLength() <= 0) return;  // Jeżeli muchomor już nie żyje, nic się nie dzieje

    std::string species = attacker->getSpecies();  // Sprawdzamy gatunek organizmu, który zjadł muchomora

    // Jeśli atakujący to roślina (Trawa, Mlecz, Toadstool), nie dzieje się nic
    if (species == "Grass" || species == "Dandelion" || species == "Toadstool") {
        return;
    } else {
        // Jeżeli organizm zjada muchomora, niezależnie od siły umiera
        std::cout << attacker->toString() << " at position " << attacker->getPosition().toString()
                  << " ate a Toadstool at " << this->getPosition().toString() << " and dies!" << std::endl;

        // Muchomor także umiera po zjedzeniu
        this->setLiveLength(0);  // Muchomor umiera po zjedzeniu
        int currentTurn = world->getCurrentTurn();  // Użyj operatora '->', bo 'world' jest wskaźnikiem

        setDeathTurn(currentTurn);
    }
}


Toadstool::Toadstool(const Toadstool& other)
    : Plant(other) {
    // Skopiuj wszystko co trzeba z `Plant`
    setSpecies(other.getSpecies());
    setInitiative(other.getInitiative());
    setLiveLength(other.getLiveLength());
    setPowerToReproduce(other.getPowerToReproduce());
    setSign(other.getSign());
}

Toadstool::Toadstool(Toadstool&& other) noexcept
    : Plant(std::move(other)) {
    setSpecies(std::move(other.getSpecies()));
    setInitiative(other.getInitiative());
    setLiveLength(other.getLiveLength());
    setPowerToReproduce(other.getPowerToReproduce());
    setSign(other.getSign());
}

Toadstool& Toadstool::operator=(const Toadstool& other) {
    if (this != &other) {
        Plant::operator=(other);  // Kopiowanie z klasy bazowej
        setSpecies(other.getSpecies());
        setInitiative(other.getInitiative());
        setLiveLength(other.getLiveLength());
        setPowerToReproduce(other.getPowerToReproduce());
        setSign(other.getSign());
    }
    return *this;
}

Toadstool& Toadstool::operator=(Toadstool&& other) noexcept {
    if (this != &other) {
        Plant::operator=(std::move(other));  // Przenoszenie z klasy bazowej
        setSpecies(std::move(other.getSpecies()));
        setInitiative(other.getInitiative());
        setLiveLength(other.getLiveLength());
        setPowerToReproduce(other.getPowerToReproduce());
        setSign(other.getSign());
    }
    return *this;
}

Toadstool::~Toadstool() {
    // jeśli coś dynamicznego byłoby w Toadstool, to tu byś to zwolnił
}


void Toadstool::draw(SDL_Renderer* renderer) {
    Position pos = getPosition();
    SDL_Rect rect = {pos.getX() * CELL_SIZE, pos.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE};

    SDL_SetRenderDrawColor(renderer, 160, 32, 240, 255); // Fioletowy
    SDL_RenderFillRect(renderer, &rect);

    SDL_SetRenderDrawColor(renderer, 50, 50, 50, 255);
    SDL_RenderDrawRect(renderer, &rect);
}
