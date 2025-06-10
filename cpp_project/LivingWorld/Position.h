#pragma once
#include <string>

class Position {
private:
    int x;
    int y;

public:
    Position(int x = 0, int y = 0);

    int getX() const;
    void setX(int x);

    int getY() const;
    void setY(int y);

    void move(int dx, int dy);
    bool isValid() const;

    double distance(Position position) const;
    std::string toString() const;
	bool operator==(const Position& other) const {
		return x == other.x && y == other.y;
	}
	
};
