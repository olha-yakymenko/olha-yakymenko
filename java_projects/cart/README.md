# Shopping Cart System for JavaMarkt

---

## 📌 Project Overview

This project is a **shopping cart system** implemented in Java for the online store **JavaMarkt**. The system manages a collection of products and supports various promotions, discounts, and special offers, including dynamic changes during runtime.

### Goals
- Implement core logic for handling products (`Product` objects)  
- Apply multiple types of promotions:
  - 5% discount for orders over 300 PLN  
  - Buy 2 products, get the 3rd cheapest free  
  - Free company mug for orders over 200 PLN  
  - One-time 30% discount coupon on a selected product  
  - Support for future, dynamic promotions  
- Sort products by price (descending) and then by name (alphabetically), with the ability to change sorting criteria at runtime  
- Provide operations on collections of products:
  - Find the cheapest/most expensive product  
  - Find N cheapest/most expensive products  
  - Sort products by price or name  
  - Calculate total price  
- Suggest the optimal application order of promotions to maximize customer savings  
- Follow SOLID principles, especially **Dependency Inversion** for sorting  
- Use **Command design pattern** for promotion application  

---

## 🛒 Product Class

Each product has the following attributes:

| Field | Type | Description |
|-------|------|-------------|
| `code` | `String` | Unique product code |
| `name` | `String` | Product name |
| `price` | `double` | Original price |
| `discountPrice` | `double` | Price after applying promotions |

---

## 🧠 Design Patterns and Principles

- **Command Pattern** — encapsulates each promotion as a separate command object  
- **Dependency Inversion / SOLID** — sorting mechanism uses interfaces (`Comparable` and `Comparator`) for flexible sorting strategies  
- **Extensibility** — new promotions can be added without modifying existing code  

---

## 🔧 Features

- Dynamic application of multiple promotions with different rules  
- Sorting products by price and/or name with runtime flexibility  
- Searching for cheapest/most expensive products or N cheapest/most expensive products  
- Calculation of total cart value, taking all promotions into account  
- Recommendation engine for the best combination and order of promotions  

---

## 🧪 Unit Tests

The project includes unit tests verifying:

- Correct price calculations and discount application  
- Sorting and searching operations on product collections  
- Proper handling and application of multiple promotions  
- Robustness against dynamic changes in available promotions  

---

## 🚀 How to Run

### Clone the repository

```bash
git clone https://github.com/olha-yakymenko/java-projects.git
cd cart
mvn clean test
