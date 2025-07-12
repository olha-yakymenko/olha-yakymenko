#pragma once
#include "Organism.h"
#include <SDL2/SDL.h>

class Plant : public Organism {
public:
    // Konstruktor z parametrami
    Plant(int power, Position position, World* world);
    
    // Konstruktor domyślny
    Plant();

    // Nadpisane metody wirtualne z klasy bazowej
    virtual void move(int dx, int dy) override;
    virtual void spread() override;
    virtual std::string toString() const override;
    virtual Organism* clone() const override; 
    virtual void collision(Organism* other) override;
    virtual void draw(SDL_Renderer* renderer) override {

    }
};
