# Java Projects Portfolio

Welcome to my **Java Projects Repository**. This repository contains three independent mini-projects demonstrating **object-oriented design**, **design patterns**, **SOLID principles**, and **unit testing**. Each project is self-contained and includes instructions for running and testing.

---

## Projects Overview

| Project | Description | Folder |
|---------|-------------|--------|
| **Market Simulation** | Turn-based market simulation with Sellers, Buyers, and a Central Bank. Models inflation, pricing, and market behavior using Observer and Visitor patterns. | `market/` |
| **Shopping Cart (JavaMarkt)** | Shopping cart system with dynamic promotions, discounts, and product sorting. Uses Command pattern to manage promotions. | `cart/` |
| **Stack & RPN Calculator** | Stack implementation for strings and an RPN calculator using the stack. Demonstrates SOLID principles and clean code practices. | `stack/` |

---

## 1️⃣ Market Simulation

### Description
- Simulates interactions between Sellers, Buyers, and a Central Bank in an economy with inflation.  
- Sellers aim to maximize profit; Buyers aim to satisfy needs at minimal cost.  
- Central Bank adjusts inflation to stabilize tax revenue.  
- Uses **Visitor** and **Observer** design patterns.

### Features
- Seller pricing based on cost, inflation, and margin  
- Buyer behavior with needs, budgets, and preference for essential/luxury goods  
- Turn-based simulation that stabilizes over time  
- Unit tests for stability, correctness, and robustness  

## 2️⃣ Shopping Cart (JavaMarkt)

### Description
- Implements a shopping cart system with dynamic promotions and discounts.
- Promotions can change during runtime; the system suggests the optimal application order.
- Uses Command design pattern for promotion logic.

### Features
- 5% discount for orders > 300 PLN
- Buy 2, get 3rd cheapest free
- Free company mug for orders > 200 PLN
- One-time 30% coupon
- Product sorting by price and name, configurable at runtime
- Unit tests verifying price calculations, sorting, and promotion application

## 3️⃣ Stack & RPN Calculator

### Description
- Implements a stack data structure and RPN calculator in Java.
- Stack stores strings with unlimited size using arrays.
- RPN calculator supports integers and operations +, -, *.

### Features
- Push, pop, and peek for strings
- Evaluation of RPN expressions using the Stack
- Handles edge cases like empty stack or invalid expressions
- SOLID and Clean Code principles applied
- Unit tests for Stack and RPN calculator