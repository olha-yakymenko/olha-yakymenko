#pragma once
#include "Plant.h"

class Dandelion : public Plant {
public:
    Dandelion(int power, Position position, World* world);
    Dandelion(int power, Position position, std::string species, World* world); 
    Dandelion();

    void spread() override;
    Dandelion* clone() const override;
    std::string toString() const override;
    void collision(Organism* other) override;
            // Rule of Five
    Dandelion(const Dandelion& other);
    Dandelion(Dandelion&& other) noexcept;
    Dandelion& operator=(const Dandelion& other);
    Dandelion& operator=(Dandelion&& other) noexcept;
    ~Dandelion();


    void draw(SDL_Renderer* renderer) override;

    private:
    void initializeAttributes();

};
