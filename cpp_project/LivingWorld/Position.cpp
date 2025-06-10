#include "Position.h"
#include <cmath>
#include <string>

Position::Position(int x, int y) : x(x >= 0 ? x : 0), y(y >= 0 ? y : 0) {}

int Position::getX() const {
    return this->x;
}

void Position::setX(int x) {
    this->x = (x >= 0) ? x : 0;
}

int Position::getY() const {
    return this->y;
}

void Position::setY(int y) {
    this->y = (y >= 0) ? y : 0;
}

void Position::move(int dx, int dy) {
    setX(getX() + dx);
    setY(getY() + dy);
}

bool Position::isValid() const {
    return x >= 0 && y >= 0;
}


double Position::distance(Position position) const {
    double dx = this->getX() - position.getX();
    double dy = this->getY() - position.getY();
    return std::sqrt((dx * dx) + (dy * dy));
}

std::string Position::toString() const {
    return "(" + std::to_string(x) + ", " + std::to_string(y) + ")";
}
