#pragma once
#include "Organism.h"
#include <SDL2/SDL.h>

class Animal : public Organism {
public:
    Animal(int power, Position position, World* world);
    Animal();

    // Metody wirtualne
    virtual void move(int dx, int dy) override;
    virtual void spread() override;
    virtual std::string toString() const override;
    virtual void collision(Organism* other) override = 0;
    virtual Animal* createOffspring(Position pos) = 0;
    virtual Animal* clone() const = 0;
    virtual std::pair<int, int> findBestMove() = 0;
    virtual void reproduce(Animal* partner);

    virtual void draw(SDL_Renderer* renderer) override;

protected:
    Position previousPosition;
};
