# Stack and RPN Calculator

---

## 📌 Project Overview

This project implements a **stack data structure** and an **RPN (Reverse Polish Notation) calculator** in Java. The stack is the core component used for evaluating arithmetic expressions in RPN format. The project emphasizes **SOLID principles**, **Clean Code**, and unit testing.

---

## 🧰 Part 1 — Stack

### Stack Features
- Stores **strings** using an internal array  
- Unlimited size (dynamic resizing)  
- Public methods:
  - `push(String value)` — pushes an element onto the stack  
  - `pop()` — removes and returns the top element; handles empty stack scenario  
  - `peek()` — returns the top element without removing it; handles empty stack scenario  

### Design Notes
- Stack is implemented using **arrays**  
- Should handle empty-stack operations gracefully  
- Unit tests are created **before implementation** to follow Test-Driven Development (TDD) principles  

---

## 🧮 Part 2 — RPN Calculator

### RPN Features
- Evaluates arithmetic expressions written in **Reverse Polish Notation**  
- Supports:
  - Integer numbers  
  - Binary operations: `+`, `-`, `*`  
- Uses the **Stack** class from Part 1 for evaluation  

### Example
Expression: `3 4 + 2 *`  
Calculation:
1. Push 3 → Stack: [3]  
2. Push 4 → Stack: [3, 4]  
3. `+` → Pop 4 and 3 → Push 7 → Stack: [7]  
4. Push 2 → Stack: [7, 2]  
5. `*` → Pop 2 and 7 → Push 14 → Stack: [14]  

Result: `14`

---

## 🧠 Design Patterns and Principles

- **SOLID principles** are applied:
  - Single Responsibility: Stack and RPN classes have separate responsibilities  
  - Open/Closed: RPN operations can be extended without modifying existing code  
  - Dependency Inversion: RPN class depends on Stack interface, not concrete implementation  
- **Clean Code**: clear naming, consistent style, small functions  

---

## 🧪 Unit Tests

Unit tests are implemented for:

- **Stack**:
  - `push`, `pop`, and `peek` behavior  
  - Handling of empty stack situations  
- **RPN Calculator**:
  - Correct calculation of various RPN expressions  
  - Handling of invalid or edge-case expressions  

---

## 🚀 How to Run

### Clone the repository

```bash
git clone https://github.com/olha-yakymenko/java_projects.git
cd java_projects/stack
mvn clean test

