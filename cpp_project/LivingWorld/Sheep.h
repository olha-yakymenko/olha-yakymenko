

#pragma once
#include "Animal.h"

class Sheep : public Animal {
public:
    Sheep(Position position, World* world);
    Sheep(int power, Position position, std::string species, World* world); // DODANE
    Sheep();

    Position getPosition() const override {
        return position;  // Jeśli nie zmieniasz logiki, wystarczy przekazać to samo
    }

    Animal* clone() const override;
    std::string toString() const override;
    void move(int dx, int dy) override;
    void collision(Organism* other) override;
    Animal* createOffspring(Position pos) override;
    std::pair<int, int> findBestMove() override;
    // void reproduce(Animal* partner) override;

    ~Sheep();
    Sheep(const Sheep& other);
    Sheep(Sheep&& other) noexcept;
    Sheep& operator=(const Sheep& other);        // operator przypisania kopiujący
    Sheep& operator=(Sheep&& other) noexcept;    // operator przypisania przenoszący

    void draw(SDL_Renderer* renderer) override;

private:
    void initializeAttributes();
};
