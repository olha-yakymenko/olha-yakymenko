# Market Simulation with Inflation, Sellers, Buyers, and Central Bank  

---

## 📌 Project Summary

### Overview
This project is a **turn-based market simulation** implemented in Java. It models interactions between three main actors — **Sellers**, **Buyers**, and a **Central Bank** — in an economy influenced by inflation. The system is intentionally simplified and designed to approach a stable state after several turns, reflecting the long-term goals of each participant.

### Goals
- Model seller pricing based on production cost, inflation, and profit margin  
- Model buyer decision-making based on needs, budget, inflation awareness, and current market prices  
- Implement a Central Bank that sets inflation to stabilize:  
  **tax revenue = inflation × market turnover**  
- Use **Observer** and **Visitor** design patterns  
- Provide unit tests verifying stability, correctness, and resilience to disturbances  

---

## 🧠 Simulation Details

### Actors

#### 🏪 Sellers
- Produce a limited quantity of products per turn  
- Set product prices based on:  
  production cost + inflation + desired margin  
- Objective: **maximize profit**

#### 🧍‍♂️ Buyers
- Have budgets, needs, and purchasing rules  
- Want to buy essential and luxury goods  
- Know the current inflation rate  
- Willingness to buy decreases as prices increase (regardless of cause)  
- Objective: **satisfy needs at the lowest cost while staying within budget**  
- Essential goods may be required to “survive” each turn  

#### 🏦 Central Bank
- Observes market prices and turnover  
- Sets and updates inflation to maintain stable tax income  
- Adapts the inflation algorithm based on market behavior of Sellers and Buyers  

---

### 🧩 Design Patterns Used

| Pattern | Purpose |
|---------|-----------|
| **Visitor** | Updates product data for Sellers and parameters for Buyers each turn |
| **Observer** | Allows passive observation and reaction to inflation and offers |

Observer relationships:

- Sellers and Buyers **observe the Central Bank** (inflation updates)  
- Buyers **observe Sellers' offers** and may react  
- Central Bank **observes Sellers and Buyers** to adjust inflation policy  

---

### 🔁 Turn Sequence (Simplified)

1. Central Bank broadcasts current inflation  
2. Sellers update prices and publish offers  
3. Buyers observe offers and purchase items  
4. Market turnover and sellers’ revenues are calculated  
5. Central Bank updates inflation to stabilize tax revenue  
6. Visitor updates data for next turn  
7. Repeat for N turns (market should approach equilibrium)  

---

### 🧪 Tests & Validation

This project includes unit tests validating:

- Correct price calculation and seller profit logic  
- Buyer decision rules (budget, needs, survival, overspending prevention)  
- Central Bank’s tax stability over multiple turns  
- Resistance to shocks (e.g., supply drop, demand spike, margin change)  

---

### 🚀 How to Run (example with Maven)

```bash
git clone https://github.com/olha-yakymenko/java_projects.git
cd java_projects/market
mvn clean test

