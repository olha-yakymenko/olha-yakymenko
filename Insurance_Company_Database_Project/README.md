# Insurance Company Database Project / Projekt Bazy Danych Towarzystwa Ubezpieczeniowego

---


### Project Overview
This project contains SQL scripts for managing an insurance company database. It includes creation of views, functions, stored procedures, and triggers to simplify data access, implement business logic, automate data operations, and enforce data validation.

### Features
- **Views:** Simplify access to client valuations, claims, and payments.
- **Functions:** Calculate discount rates and insurance costs.
- **Procedures:** Add claims and update claim statuses.
- **Triggers:** Automatically update policy prices, approve claim statuses, and validate policy amounts against valuations.

### Usage
- Run the script in a PostgreSQL environment.
- The views provide simplified querying for clients and insurance centers.
- Use the functions and procedures to perform business operations.
- Triggers ensure automatic updates and data integrity.

### Notes
- Make sure all referenced tables (`Klient`, `Wycena`, `Zdarzenie`, `Polis`, etc.) exist in the database before executing the script.
- Adjust parameters in function and procedure calls according to your data.

---


### Opis projektu
Projekt zawiera skrypty SQL do zarządzania bazą danych towarzystwa ubezpieczeniowego. Skrypty tworzą widoki, funkcje, procedury składowane oraz wyzwalacze, które upraszczają dostęp do danych, implementują logikę biznesową, automatyzują operacje oraz zapewniają walidację danych.

### Funkcjonalności
- **Widoki:** Uproszczony dostęp do wycen klientów, zgłoszeń szkód i wypłat.
- **Funkcje:** Obliczanie wysokości rabatu oraz kosztu ubezpieczenia.
- **Procedury:** Dodawanie zgłoszeń oraz zmiana statusów zgłoszeń.
- **Wyzwalacze:** Automatyczna aktualizacja cen polis, zatwierdzanie statusów zgłoszeń oraz walidacja kwot polis względem wycen.

### Użytkowanie
- Uruchomić skrypt w środowisku PostgreSQL.
- Widoki umożliwiają uproszczone zapytania do danych klientów i ośrodków ubezpieczeniowych.
- Funkcje i procedury służą do wykonywania operacji biznesowych.
- Wyzwalacze gwarantują automatyczne aktualizacje i integralność danych.

### Uwagi
- Upewnij się, że wszystkie wymagane tabele (`Klient`, `Wycena`, `Zdarzenie`, `Polis` itd.) istnieją w bazie przed wykonaniem skryptu.
- Dostosuj parametry w wywołaniach funkcji i procedur do posiadanych danych.

---

