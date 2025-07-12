#pragma once

#include <vector>
#include <ctime>
#include "Organism.h"
#include <SDL2/SDL.h>

using namespace std;

class World
{
private:
	int worldX;
	int worldY;
	int turn = 0;
	std::vector<Organism*> organisms;
	char separator = '.';

	string getOrganismFromPosition(int x, int y);
	bool isPositionOnWorld(int x, int y);
	

public:
	World(int worldX, int worldY);
	World() : World(6, 6) {};

	int getWorldX();
	void setWorldX(int worldX);
	int getWorldY();
	void setWorldY(int worldY);
	
	void addOrganism(Organism *organism);
	vector<Position> getVectorOfFreePositionsAround(Position position);
	// void makeTurn();

	void makeTurn(SDL_Renderer* renderer);  // Zmieniona deklaracja
    void renderWorld(SDL_Renderer* renderer);

	void writeWorld(string fileName);
	void readWorld(string fileName);
	// void resetWorld()
	
	std::string toString() const;

	bool isPositionFree(Position position);
	Organism* getOrganismAt(const Position& pos) const;
	Position findEmptyAdjacentPosition(Position position);
	void removeOrganismAtPosition(Position pos);
	void removeDeadOrganisms();
	
	Organism* getOrganismFromPosition(Position pos);
	std::vector<Organism*> getOrganisms();
	int getCurrentTurn() const { return turn; }
	void handleDeath(Organism* organism);

};
