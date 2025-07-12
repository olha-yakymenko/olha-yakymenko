#include <SDL2/SDL.h>
#include <iostream>
#include <cstdlib>
#include <ctime>
#include <vector>
#include "Position.h"
#include "Organism.h"
#include "Plant.h"
#include "Animal.h"
#include "Grass.h"
#include "Sheep.h"
#include "Dandelion.h"
#include "Wolf.h"
#include "World.h"
#include "Toadstool.h"
#include "Config.h"

using namespace std;

const int SCREEN_WIDTH = 800;
const int SCREEN_HEIGHT = 600;
const int CELL_SIZE = 15;
const int GRID_WIDTH = SCREEN_WIDTH / CELL_SIZE;
const int GRID_HEIGHT = SCREEN_HEIGHT / CELL_SIZE;

const int FPS = 10;
const int FRAME_DELAY = 1000 / FPS;
const int MAX_TURNS = 1000;

// Funkcja do inicjalizacji SDL2
bool init(SDL_Window*& window, SDL_Renderer*& renderer) {
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        cout << "SDL could not initialize! SDL_Error: " << SDL_GetError() << endl;
        return false;
    }
    
    window = SDL_CreateWindow("World Simulator", SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
                              SCREEN_WIDTH, SCREEN_HEIGHT, SDL_WINDOW_SHOWN);
    if (!window) {
        cout << "Window could not be created! SDL_Error: " << SDL_GetError() << endl;
        return false;
    }
    
    renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
    if (!renderer) {
        cout << "Renderer could not be created! SDL_Error: " << SDL_GetError() << endl;
        return false;
    }

    return true;
}

// Czyszczenie zasobów
void close(SDL_Window* window, SDL_Renderer* renderer) {
    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
}

// Losowa pozycja
Position getRandomPosition() {
    return {rand() % GRID_WIDTH, rand() % GRID_HEIGHT};
}

// Dodawanie organizmów
void populateWorld(World& world) {
    for (int i = 0; i < 30; ++i)
        world.addOrganism(new Grass(5, getRandomPosition(), &world));

    for (int i = 0; i < 15; ++i)
        world.addOrganism(new Sheep(getRandomPosition(), &world));

    for (int i = 0; i < 5; ++i)
        world.addOrganism(new Wolf(getRandomPosition(), &world));

    for (int i = 0; i < 10; ++i)
        world.addOrganism(new Dandelion(3, getRandomPosition(), &world));

    for (int i = 0; i < 5; ++i)
        world.addOrganism(new Toadstool(4, getRandomPosition(), &world));
}

// Rysowanie organizmów
// void renderOrganisms(SDL_Renderer* renderer, World& world) {
//     for (Organism* org : world.getOrganisms()) {
//         if (!org) continue;
//         if (org->getLiveLength() > 0) {
//             Position pos = org->getPosition();
//             SDL_Rect rect = {pos.getX() * CELL_SIZE, pos.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE};

//             if (dynamic_cast<Grass*>(org))
//                 SDL_SetRenderDrawColor(renderer, 34, 139, 34, 255); // Zielony
//             else if (dynamic_cast<Sheep*>(org))
//                 SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255); // Biały
//             else if (dynamic_cast<Wolf*>(org))
//                 SDL_SetRenderDrawColor(renderer, 139, 0, 0, 255); // Czerwony
//             else if (dynamic_cast<Dandelion*>(org))
//                 SDL_SetRenderDrawColor(renderer, 255, 255, 0, 255); // Żółty
//             else if (dynamic_cast<Toadstool*>(org))
//                 SDL_SetRenderDrawColor(renderer, 160, 32, 240, 255); // Fioletowy

//             SDL_RenderFillRect(renderer, &rect);
//             SDL_SetRenderDrawColor(renderer, 50, 50, 50, 255);
//             SDL_RenderDrawRect(renderer, &rect);
//         }
//     }
// }

void renderOrganisms(SDL_Renderer* renderer, World& world) {
    for (Organism* org : world.getOrganisms()) {
        if (org && org->getLiveLength() > 0) {
            org->draw(renderer); // Wywołuje draw() konkretnego typu organizmu!
        }
    }
}


int main() {
    srand(static_cast<unsigned int>(time(nullptr)));

    SDL_Window* window = nullptr;
    SDL_Renderer* renderer = nullptr;

    if (!init(window, renderer)) {
        close(window, renderer);
        return -1;
    }

    World world;
    populateWorld(world);

    bool quit = false;
    SDL_Event e;
    int turnCount = 0;

    Uint32 lastTurnTime = SDL_GetTicks();    
    const Uint32 TURN_INTERVAL = 1000;       

    

    // while (!quit && turnCount < MAX_TURNS) {
    //     Uint32 frameStart = SDL_GetTicks();

    //     while (SDL_PollEvent(&e)) {
    //         if (e.type == SDL_QUIT) {
    //             quit = true;
    //         }
    //     }

    //     SDL_SetRenderDrawColor(renderer, 240, 240, 240, 255); // Jasne tło
    //     SDL_RenderClear(renderer);

    //     world.makeTurn(renderer); // Ruch organizmów
    //     cout << "Turn: " << (turnCount + 1) << " Organisms: " << world.getOrganisms().size() << endl;

    //     renderOrganisms(renderer, world);
    //     SDL_RenderPresent(renderer);

    //     turnCount++;

    //     Uint32 frameTime = SDL_GetTicks() - frameStart;
    //     if (frameTime < FRAME_DELAY) {
    //         SDL_Delay(FRAME_DELAY - frameTime);
    //     }
    // }

    while (!quit && turnCount < MAX_TURNS) {
        Uint32 frameStart = SDL_GetTicks();
    
        while (SDL_PollEvent(&e)) {
            if (e.type == SDL_QUIT) {
                quit = true;
            }
        }
    
        Uint32 currentTime = SDL_GetTicks();
        if (currentTime - lastTurnTime >= TURN_INTERVAL) {
            world.makeTurn(renderer);
            turnCount++;
            lastTurnTime = currentTime;
            cout << "Turn: " << turnCount << " Organisms: " << world.getOrganisms().size() << endl;
        }
    
        SDL_SetRenderDrawColor(renderer, 240, 240, 240, 255);
        SDL_RenderClear(renderer);
    
        // Nadpisanie całej siatki tłem
        for (int y = 0; y < GRID_HEIGHT; ++y) {
            for (int x = 0; x < GRID_WIDTH; ++x) {
                SDL_Rect rect = {x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE};
                SDL_SetRenderDrawColor(renderer, 240, 240, 240, 255);
                SDL_RenderFillRect(renderer, &rect);
            }
        }
    
        renderOrganisms(renderer, world);
        SDL_RenderPresent(renderer);
    
        Uint32 frameTime = SDL_GetTicks() - frameStart;
        if (frameTime < FRAME_DELAY) {
            SDL_Delay(FRAME_DELAY - frameTime);
        }
    }
    

    cout << "Symulacja zakończona po " << turnCount << " turach." << endl;

    close(window, renderer);
    return 0;
}
