#pragma once

#include "Position.h"
#include <string>
#include <SDL2/SDL.h>
#include <vector>

class World;  

struct Ancestor {
  int birthTurn;
  int deathTurn;


  // Default constructor
  Ancestor() : birthTurn(0), deathTurn(-1) {}

  Ancestor(int birthTurn, int deathTurn)
      : birthTurn(birthTurn), deathTurn(deathTurn) {}

};


class Organism {
public:
    // Konstruktor domyślny
    Organism();
    
    // Konstruktor z parametrami
    Organism(int power, Position position, std::string species,
      int initiative, int liveLength, int powerToReproduce,
      char sign, World* world);


    // Wirtualny destruktor
    virtual ~Organism() = default;

    // Wirtualne metody, które muszą zostać zaimplementowane w klasach dziedziczących
    virtual void move(int dx, int dy) = 0;
    virtual Organism* clone() const = 0;

    // Gettery i settery
    virtual int getPower() const;
    virtual void setPower(int power);

    virtual Position getPosition() const;
    virtual void setPosition(Position position);

    // Metoda spread() jest wirtualna
    virtual void spread();  // Implementacja zależna od klasy dziedziczącej

    // Metoda toString() jest wirtualna
    virtual std::string toString() const;

    // Gettery i settery dla pozostałych pól
    virtual std::string getSpecies() const;
    virtual void setSpecies(const std::string& spec);

    virtual int getInitiative() const;
    virtual void setInitiative(int initiative);

    virtual int getLiveLength() const;
    virtual void setLiveLength(int liveLength);

    virtual int getPowerToReproduce() const;
    virtual void setPowerToReproduce(int powerToReproduce);

    virtual char getSign() const;
    virtual void setSign(char sign);

    virtual World* getWorld() const;
    virtual void setWorld(World* world);

    virtual bool isAlive() const;
    virtual void increasePower();
    virtual void decreaseLife();

	virtual void collision(Organism* other) = 0;

  virtual void draw(SDL_Renderer* renderer) = 0;

  Organism& operator=(const Organism& other);
Organism& operator=(Organism&& other) noexcept;


  // Konstruktor kopiujący
Organism(const Organism& other);

// Konstruktor przenoszący
Organism(Organism&& other) noexcept;

virtual void setAncestorsHistory(const std::vector<Ancestor>& history);
virtual int getBirthTurn() const { return birthTurn; }
virtual void setBirthTurn(int turn) { birthTurn = turn; }

virtual void addAncestor(int birthTurn, int deathTurn);

virtual void setDeathTurn(int deathTurn);
virtual const std::vector<Ancestor>& getAncestorsHistory() const;

// Dodaj te deklaracje do pliku Organism.h
virtual int getDeathTurn() const;
virtual void updateAncestorDeathTurn();

protected:
    int power;
    Position position;
    std::string species;
    int initiative;
    int liveLength;
    int powerToReproduce;
    int initialPowerToReproduce; 
    char sign;
    World* world;

    std::vector<Ancestor> ancestorsHistory;
    int birthTurn;
    int deathTurn;
    Position lastPosition;
};
