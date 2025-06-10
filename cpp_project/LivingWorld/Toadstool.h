#pragma once
#include "Plant.h"

class Toadstool : public Plant {
public:
    Toadstool(int power, Position position, World* world);
    Toadstool(int power, Position position, std::string species, World* world); // DODANE
    Toadstool();

    void spread() override;
    Toadstool* clone() const override;
    std::string toString() const override;
    void collision(Organism* other) override;

        // Rule of Five
        Toadstool(const Toadstool& other);                  // konstruktor kopiujący
        Toadstool(Toadstool&& other) noexcept;              // konstruktor przenoszący
        Toadstool& operator=(const Toadstool& other);       // operator przypisania kopiujący
        Toadstool& operator=(Toadstool&& other) noexcept;   // operator przypisania przenoszący
        ~Toadstool();    
        
        void initializeAttributes();// destruktor

        void draw(SDL_Renderer* renderer) override;

    
};
