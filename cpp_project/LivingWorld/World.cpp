#include "World.h"
#include <iostream>
#include <fstream>
#include <algorithm> 
#include "Animal.h"
#include "Plant.h"
#include "Grass.h"
#include <string>
#include <fstream>
#include "Sheep.h"
#include <cstdlib>
#include "Dandelion.h"
#include "Wolf.h"
#include "Toadstool.h"
#include <SDL2/SDL.h>


World::World(int x, int y) : worldX(x), worldY(y) {}

void World::addOrganism(Organism* organism) {
    if (organism != nullptr) {
        organisms.push_back(organism);
    } else {
        std::cerr << "Error: Trying to add a null organism!" << std::endl;
    }
}

void World::makeTurn(SDL_Renderer* renderer) {
    std::vector<Position> newPositions;
    int numberOfNewPositions;
    int randomIndex;

    // Aktualizacja statystyk wszystkich organizmów
    for (Organism* org : organisms) {
        if (org && org->getLiveLength() > 0) {
            org->setPower(org->getPower() + 1);
            org->setLiveLength(org->getLiveLength() - 1);
        }

        if (org->getLiveLength() <= 0 && org->getDeathTurn() == -1) {
            org->setDeathTurn(turn);  // Ustawiamy turę śmierci na bieżącą turę
            handleDeath(org);  // aktualizacja historii przodków

        }
    }

    // Sortowanie organizmów wg inicjatywy i siły
    std::sort(organisms.begin(), organisms.end(), [](Organism* a, Organism* b) {
        if (a->getInitiative() != b->getInitiative())
            return a->getInitiative() > b->getInitiative();
        return a->getPower() > b->getPower();
    });

    // Pierwszy etap - ruch, rozprzestrzenianie, rozmnażanie
    for (Organism* org : organisms) {
        if (org == nullptr || org->getLiveLength() <= 0) {
            continue;  // Pomiń martwe organizmy
        }

        if (Animal* animal = dynamic_cast<Animal*>(org)) {
            // Znajdź potencjalnego partnera do rozmnażania
            Organism* partner = nullptr;
            for (Organism* other : organisms) {
                if (other == animal || other->getLiveLength() <= 0) continue;
                if (animal->getSpecies() == other->getSpecies() &&
                    abs(animal->getPosition().getX() - other->getPosition().getX()) <= 1 &&
                    abs(animal->getPosition().getY() - other->getPosition().getY()) <= 1) {
                    partner = other;
                    break;
                }
            }

            // Rozmnażanie
            if (partner) {
                animal->reproduce(dynamic_cast<Animal*>(partner));
            }

            // Ruch
            newPositions = getVectorOfFreePositionsAround(animal->getPosition());
            numberOfNewPositions = newPositions.size();

            if (numberOfNewPositions > 0) {
                randomIndex = rand() % numberOfNewPositions;
                int dx = newPositions[randomIndex].getX() - animal->getPosition().getX();
                int dy = newPositions[randomIndex].getY() - animal->getPosition().getY();
                animal->move(dx, dy);

                // Sprawdzenie, czy zwierzę przeżyło po ruchu
                if (animal->getLiveLength() <= 0) continue;
            }
        }
        else if (Plant* plant = dynamic_cast<Plant*>(org)) {
            if (plant->getLiveLength() > 0) {
                plant->spread();
            }
        }
    }

    // Drugi etap - kolizje
    for (size_t i = 0; i < organisms.size(); ++i) {
        Organism* org1 = organisms[i];
        if (org1->getLiveLength() <= 0) continue;

        for (size_t j = i + 1; j < organisms.size(); ++j) {
            Organism* org2 = organisms[j];
            if (org2->getLiveLength() <= 0) continue;

            Position pos1 = org1->getPosition();
            Position pos2 = org2->getPosition();

            if ((pos1 == pos2) || (abs(pos1.getX() - pos2.getX()) <= 1 && abs(pos1.getY() - pos2.getY()) <= 1)) {
                org1->collision(org2);
                if (org1->getLiveLength() <= 0) break;  // Jeśli org1 umiera, przerwij
                org2->collision(org1);  // Zrób kolizję w drugą stronę
                if (org2->getLiveLength() <= 0) break;  // Jeśli org2 umiera, przerwij
            }
        }
    }

    // Trzeci etap - czyszczenie martwych organizmów
    removeDeadOrganisms();

    // Zwiększamy licznik tury
    turn++;

    // Rysowanie stanu świata na ekranie
    renderWorld(renderer);
}



void World::renderWorld(SDL_Renderer* renderer) {
    // Czyszczenie ekranu
    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255); // Biały kolor
    SDL_RenderClear(renderer);

    // Rysowanie organizmów
    for (Organism* org : organisms) {
        if (org->getLiveLength() > 0) {
            Position pos = org->getPosition();
            SDL_Rect rect = { pos.getX() * 20, pos.getY() * 20, 20, 20 }; // Przeskalowane do większych jednostek
            if (dynamic_cast<Grass*>(org)) {
                SDL_SetRenderDrawColor(renderer, 0, 255, 0, 255); // Zielona trawa
            } else if (dynamic_cast<Sheep*>(org)) {
                SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255); // Białe owce
            } else if (dynamic_cast<Wolf*>(org)) {
                SDL_SetRenderDrawColor(renderer, 255, 0, 0, 255); // Czerwony wilk
            }
            SDL_RenderFillRect(renderer, &rect);
        }
    }

    // Aktualizacja ekranu
    SDL_RenderPresent(renderer);
}



std::string World::toString() const {
    std::string result = "Turn: " + std::to_string(turn) + "\n";
    for (int y = 0; y < worldY; ++y) {
        for (int x = 0; x < worldX; ++x) {
            bool organismFound = false;
            for (auto org : organisms) {
                if (org->getPosition().getX() == x && org->getPosition().getY() == y) {
                    result += org->toString();
                    organismFound = true;
                    break;
                }
            }
            if (!organismFound) {
                result += ".";  // Pusta przestrzeń
            }
        }
        result += "\n";
    }
    return result;
}

std::vector<Position> World::getVectorOfFreePositionsAround(Position position) {
    std::vector<Position> freePositions;
    for (int dx = -1; dx <= 1; ++dx) {
        for (int dy = -1; dy <= 1; ++dy) {
            if (dx != 0 || dy != 0) {
                Position newPos(position.getX() + dx, position.getY() + dy);
                if (isPositionFree(newPos)) {
                    freePositions.push_back(newPos);
                }
            }
        }
    }
    return freePositions;
}

bool World::isPositionFree(Position position) {
    for (auto org : organisms) {
        if (org->getPosition().getX() == position.getX() && org->getPosition().getY() == position.getY()) {
            return false;
        }
    }
    return true;
}

void World::writeWorld(std::string fileName) {
    std::fstream my_file;
    my_file.open(fileName, std::ios::out | std::ios::binary);
    if (my_file.is_open()) {
        my_file.write((char*)&this->worldX, sizeof(int));
        my_file.write((char*)&this->worldY, sizeof(int));
        my_file.write((char*)&this->turn, sizeof(int));

        int orgs_size = this->organisms.size();
        my_file.write((char*)&orgs_size, sizeof(int));

        for (int i = 0; i < orgs_size; i++) {
            int data;
            data = this->organisms[i]->getPower();  // Zmiana kropki na strzałkę
            my_file.write((char*)&data, sizeof(int));

            data = this->organisms[i]->getPosition().getX();  // Zmiana kropki na strzałkę
            my_file.write((char*)&data, sizeof(int));

            data = this->organisms[i]->getPosition().getY();  // Zmiana kropki na strzałkę
            my_file.write((char*)&data, sizeof(int));

            std::string s_data = this->organisms[i]->getSpecies();  // Zmiana kropki na strzałkę
            int s_size = s_data.size();
            my_file.write((char*)&s_size, sizeof(int));
            my_file.write(s_data.data(), s_data.size());
        }

        my_file.close();
    }
}


void World::readWorld(std::string fileName) {
    std::fstream my_file;
    my_file.open(fileName, std::ios::in | std::ios::binary);

    if (my_file.is_open()) {
        int result;

        // Read basic world data
        my_file.read((char*)&result, sizeof(int));
        this->worldX = result;
        my_file.read((char*)&result, sizeof(int));
        this->worldY = result;
        my_file.read((char*)&result, sizeof(int));
        this->turn = result;
        my_file.read((char*)&result, sizeof(int));
        int orgs_size = result;

        // Vector of pointers to organisms
        std::vector<Organism*> new_organisms;

        for (int i = 0; i < orgs_size; ++i) {
            int power;
            my_file.read((char*)&result, sizeof(int));
            power = result;

            int pos_x, pos_y;
            my_file.read((char*)&result, sizeof(int));
            pos_x = result;
            my_file.read((char*)&result, sizeof(int));
            pos_y = result;
            Position pos{ pos_x, pos_y };

            int s_size;
            my_file.read((char*)&result, sizeof(int));
            s_size = result;

            std::string species;
            species.resize(s_size);
            my_file.read(&species[0], s_size);

            Organism* org = nullptr;

            if (species == "Sheep") {
                org = new Sheep(power, pos, species, this);
            } else if (species == "Grass") {
                org = new Grass(power, pos, species, this);
			} else if (species == "Dandelion") {
                org = new Dandelion(power, pos, species, this);
            } else if (species == "Wolf") {
                org = new Wolf(power, pos, species, this);
            }else if (species == "Toadstool") {
                org = new Toadstool(power, pos, species, this);
            }  else {
                std::cerr << "Unknown species: " << species << std::endl;
                continue; // skip unknown
            }

            new_organisms.push_back(org);
        }

        this->organisms = new_organisms;
        my_file.close();
    } else {
        std::cerr << "Failed to open file: " << fileName << std::endl;
    }
}




Position World::findEmptyAdjacentPosition(Position position) {
    for (int dx = -1; dx <= 1; ++dx) {
        for (int dy = -1; dy <= 1; ++dy) {
            if (dx == 0 && dy == 0) continue;  // Pomijamy samą pozycję

            Position newPos(position.getX() + dx, position.getY() + dy);
            if (isPositionFree(newPos)) {
                return newPos;  // Zwracamy pierwszą wolną pozycję
            }
        }
    }
    return Position(-1, -1);  // Brak wolnej pozycji
}

Organism* World::getOrganismAt(const Position& pos) const {
    for (Organism* org : organisms) {
        if (org->getPosition() == pos && org->isAlive()) {
            std::cout << "Found organism at: " << pos.toString() << ", Species: " << org->getSpecies() << std::endl;
            return org;
        }
    }
    std::cout << "No organism found at: " << pos.toString() << std::endl;
    return nullptr;  // jeśli brak organizmu na tej pozycji
}

Organism* World::getOrganismFromPosition(Position pos) {
    for (auto& organism : organisms) {
        if (organism->getPosition() == pos) {
            return organism;  // Zwracamy organizm, który jest na tej pozycji
        }
    }
    return nullptr;  // Zwracamy nullptr, jeśli na tej pozycji nie ma organizmu
}


void World::removeOrganismAtPosition(Position pos) {
    for (auto it = organisms.begin(); it != organisms.end(); ++it) {
        Organism* organism = *it;
        if (organism->getPosition() == pos) {
            std::cout << "Removing organism at: " << pos.toString() << std::endl;  // Debugowanie
            organisms.erase(it);  // Usuwamy organizm z planszy
            break;
        }
    }
}


void World::removeDeadOrganisms() {
    for (auto it = organisms.begin(); it != organisms.end(); ) {
        if ((*it)->getLiveLength() <= 0) {
            std::cout << (*it)->getSpecies() << " at " << (*it)->getPosition().toString() << " has died.\n";
            it = organisms.erase(it);  // usuń z wektora i przejdź do kolejnego
        } else {
            ++it;
        }
    }
}

std::vector<Organism*> World::getOrganisms() {
    return organisms;
}


void World::handleDeath(Organism* organism) {
    int currentTurn = getCurrentTurn();  // Zdobądź bieżącą turę

    // Ustawienie tury śmierci
    organism->setDeathTurn(currentTurn);

    // Zaktualizowanie historii przodków
    organism->updateAncestorDeathTurn();


}