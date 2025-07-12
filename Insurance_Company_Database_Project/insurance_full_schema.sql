-- Creating table Address
-- Tworzenie tabeli Adres
CREATE TABLE Adres (
    idAdres SERIAL PRIMARY KEY,
    ulica VARCHAR(100),
    numer_domu VARCHAR(10),
    kod_pocztowy VARCHAR(10),
    miasto VARCHAR(50)
);

-- Inserting records into Address table
-- Wstawianie rekordów do tabeli Adres
INSERT INTO Adres (ulica, numer_domu, kod_pocztowy, miasto) VALUES
    ('Main Street', '123', '12345', 'Anytown'),
    ('Oak Avenue', '456', '54321', 'Otherville'),
    ('Elm Road', '789', '98765', 'Mapleton'),
    ('Pine Lane', '101', '13579', 'Springside'),
    ('Maple Drive', '202', '24680', 'Riverside');

-- Creating table Client
-- Tworzenie tabeli Klient
CREATE TABLE Klient (
    idKlient SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    data_urodzenia DATE,
    pesel VARCHAR(11) UNIQUE,
    idAdres INT NOT NULL,
    FOREIGN KEY (idAdres) REFERENCES Adres(idAdres)
);

-- Inserting records into Client table
-- Wstawianie rekordów do tabeli Klient
INSERT INTO Klient (imie, nazwisko, data_urodzenia, pesel, idAdres) VALUES
    ('John', 'Doe', '1985-03-15', '85031512345', 1),
    ('Jane', 'Smith', '1990-07-20', '90072098765', 2),
    ('Michael', 'Johnson', '1976-11-10', '76111054321', 3),
    ('Emily', 'Williams', '1988-05-03', '88050365432', 4),
    ('Daniel', 'Brown', '1995-09-25', '95092578901', 5);

-- Creating table Contact
-- Tworzenie tabeli Kontakt
CREATE TABLE Kontakt (
    idKontakt SERIAL PRIMARY KEY,
    numer_telefonu VARCHAR(15),
    email VARCHAR(50)
);

-- Inserting records into Contact table
-- Wstawianie rekordów do tabeli Kontakt
INSERT INTO Kontakt (numer_telefonu, email) VALUES
    ('123456789', 'john.doe@example.com'),
    ('987654321', 'jane.smith@example.com'),
    ('111222333', 'michael.johnson@example.com'),
    ('444555666', 'emily.williams@example.com'),
    ('777888999', 'daniel.brown@example.com');

-- Creating table Osrodek_ubezpieczeniowy (Insurance_Office)
-- Tworzenie tabeli Osrodek_ubezpieczeniowy
CREATE TABLE Osrodek_ubezpieczeniowy (
    idOsrodek_ubezpieczeniowy SERIAL PRIMARY KEY,
    idAdres INT NOT NULL,
    idKontakt INT NOT NULL,
    FOREIGN KEY (idAdres) REFERENCES Adres(idAdres),
    FOREIGN KEY (idKontakt) REFERENCES Kontakt(idKontakt)
);

-- Inserting records into Osrodek_ubezpieczeniowy (Insurance_Office) table
-- Wstawianie rekordów do tabeli Osrodek_ubezpieczeniowy
INSERT INTO Osrodek_ubezpieczeniowy (idAdres, idKontakt) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

-- Creating table Wycena (Valuation)
-- Tworzenie tabeli Wycena
CREATE TABLE Wycena (
    idWycena SERIAL PRIMARY KEY,
    data_wyceny DATE NOT NULL,
    kwota NUMERIC(15, 2) NOT NULL,
    opis TEXT
);

-- Inserting records into Valuation table
INSERT INTO Wycena (data_wyceny, kwota, opis) VALUES
    ('2024-05-01', 500.00, 'Wycena ubezpieczenia samochodu'),
    ('2024-05-05', 700.00, 'Wycena ubezpieczenia mieszkania'),
    ('2024-05-10', 1000.00, 'Wycena ubezpieczenia na życie'),
    ('2024-05-15', 1200.00, 'Wycena ubezpieczenia działalności gospodarczej'),
    ('2024-05-20', 1500.00, 'Wycena ubezpieczenia zdrowotnego');

-- Creating table Insured_Item(Przedmiot_ubezpieczenia)
-- Tworzenie tabeli Przedmiot_ubezpieczenia
CREATE TABLE Przedmiot_ubezpieczenia (
    idPrzedmiot_ubezpieczenia SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) NOT NULL,
    opis TEXT,
    idKlient INT NOT NULL,
    idWycena INT NOT NULL, 
    FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
    FOREIGN KEY (idWycena) REFERENCES Wycena(idWycena) 
);


-- Inserting records into Przedmiot_ubezpieczenia (Insured_Item) table
-- Wstawianie rekordów do tabeli Przedmiot_ubezpieczenia
INSERT INTO Przedmiot_ubezpieczenia (nazwa, opis, idKlient, idWycena) VALUES
    ('Samochód', 'Ubezpieczenie komunikacyjne', 1, 1),
    ('Dom', 'Ubezpieczenie mieszkania', 2, 2),
    ('Zdrowie', 'Ubezpieczenie zdrowotne', 3, 3),
    ('Życie', 'Ubezpieczenie na życie', 4, 4),
    ('Firma', 'Ubezpieczenie działalności gospodarczej', 5, 5);

-- Creating table Agent
-- Tworzenie tabeli Agent
CREATE TABLE Agent (
    idAgent SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    numer_pesel VARCHAR(11) UNIQUE,
    stanowisko VARCHAR(50) NOT NULL,
    idosrodek_ubezpieczeniowy INT NOT NULL,
    FOREIGN KEY (idosrodek_ubezpieczeniowy) REFERENCES Osrodek_ubezpieczeniowy(idOsrodek_ubezpieczeniowy)
);

-- Inserting records into Agent table
-- Wstawianie rekordów do tabeli Agent
INSERT INTO Agent (imie, nazwisko, numer_pesel, stanowisko, idosrodek_ubezpieczeniowy) VALUES
    ('Adam', 'Kowalski', '80010112345', 'Agent ubezpieczeniowy', 1),
    ('Anna', 'Nowak', '81020223456', 'Agent ubezpieczeniowy', 2),
    ('Piotr', 'Wiśniewski', '82030334567', 'Agent ubezpieczeniowy', 3),
    ('Magda', 'Kaczmarek', '83040445678', 'Agent ubezpieczeniowy', 4),
    ('Tomasz', 'Lewandowski', '84050556789', 'Agent ubezpieczeniowy', 5);

-- Creating table Polis (Policy)
-- Tworzenie tabeli Polis
CREATE TABLE Polis (
    idPolis SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    nazwa VARCHAR(50) NOT NULL,
    opis TEXT NOT NULL,
    cena NUMERIC(15, 2) NOT NULL,
    FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
    FOREIGN KEY (idAgent) REFERENCES Agent(idAgent)
);

-- Inserting records into Polis (Policy) table
-- Wstawianie rekordów do tabeli Polis
INSERT INTO Polis (idKlient, idAgent, nazwa, opis, cena) VALUES
    (1, 1, 'Polisa komunikacyjna', 'Polisa ubezpieczenia samochodu', 500.00),
    (2, 2, 'Polisa mieszkania', 'Polisa ubezpieczenia domu', 800.00),
    (3, 3, 'Polisa zdrowotna', 'Polisa ubezpieczenia zdrowotnego', 1200.00),
    (4, 4, 'Polisa na życie', 'Polisa ubezpieczenia na życie', 1500.00),
    (5, 5, 'Polisa firmowa', 'Polisa ubezpieczenia działalności gospodarczej', 2000.00);

-- Creating table Rodzaj_ubezpieczenia (Insurance_Type)
-- Tworzenie tabeli Rodzaj_ubezpieczenia
CREATE TABLE Rodzaj_ubezpieczenia (
    idRodzaj_ubezpieczenia SERIAL PRIMARY KEY,
    idPolis INT NOT NULL,
    nazwa VARCHAR(100),
    opis TEXT,
    FOREIGN KEY (idPolis) REFERENCES Polis(idPolis)
);

-- Inserting records into Rodzaj_ubezpieczenia (Insurance_Type) table
-- Wstawianie rekordów do tabeli Rodzaj_ubezpieczenia
INSERT INTO Rodzaj_ubezpieczenia (idPolis, nazwa, opis) VALUES
    (1, 'OC', 'Ubezpieczenie od odpowiedzialności cywilnej'),
    (2, 'NNW', 'Ubezpieczenie następstw nieszczęśliwych wypadków'),
    (3, 'NFZ', 'Ubezpieczenie finansowane przez Narodowy Fundusz Zdrowia'),
    (4, 'Śmierć', 'Ubezpieczenie na wypadek śmierci'),
    (5, 'OC', 'Ubezpieczenie od odpowiedzialności cywilnej');

-- Creating table Zdarzenie (Event)
-- Tworzenie tabeli Zdarzenie
CREATE TABLE Zdarzenie (
    idZdarzenie SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    data_zgloszenia DATE NOT NULL,
    typ VARCHAR(50),
    status_obslugi VARCHAR(100),
    FOREIGN KEY (idKlient) REFERENCES Klient(idKlient)
);

-- Inserting records into Zdarzenie (Event) table
-- Wstawianie rekordów do tabeli Zdarzenie
INSERT INTO Zdarzenie (idKlient, data_zgloszenia, typ, status_obslugi) VALUES
    (1, '2024-05-01', 'Awaria pojazdu', 'W trakcie obsługi'),
    (2, '2024-05-05', 'Uszkodzenie mienia', 'Oczekujące na decyzję'),
    (3, '2024-05-10', 'Wizyta lekarska', 'Zakończone'),
    (4, '2024-05-15', 'Zgłoszenie śmierci', 'W trakcie obsługi'),
    (5, '2024-05-20', 'Rozpoczęcie działalności', 'Nowe zgłoszenie');

-- Creating table Wyplata (Payment)
-- Tworzenie tabeli Wyplata
CREATE TABLE Wyplata (
    idWyplata SERIAL PRIMARY KEY,
    idZdarzenie INT NOT NULL,
    data_zgloszenia DATE NOT NULL,
    kwota NUMERIC(15, 2) NOT NULL,
    idOsrodek_ubezpieczeniowy INT NOT NULL,
    data_wyplaty DATE NOT NULL,
    sposob_wyplaty VARCHAR(50) NOT NULL,
    FOREIGN KEY (idZdarzenie) REFERENCES Zdarzenie(idZdarzenie),
    FOREIGN KEY (idOsrodek_ubezpieczeniowy) REFERENCES Osrodek_ubezpieczeniowy(idOsrodek_ubezpieczeniowy)
);

-- Inserting records into Wyplata (Payment) table
-- Wstawianie rekordów do tabeli Wyplata
INSERT INTO Wyplata (idZdarzenie, data_zgloszenia, kwota, idOsrodek_ubezpieczeniowy, data_wyplaty, sposob_wyplaty) VALUES
    (1, '2024-05-02', 200.00, 1, '2024-05-10', 'Przelew'),
    (2, '2024-05-06', 300.00, 2, '2024-05-12', 'Gotówka'),
    (3, '2024-05-11', 400.00, 3, '2024-05-18', 'Przelew'),
    (4, '2024-05-16', 500.00, 4, '2024-05-24', 'Przelew'),
    (5, '2024-05-21', 600.00, 5, '2024-05-28', 'Gotówka');

-- Creating table Discount (Rabat)
-- Tworzenie tabeli Rabat
CREATE TABLE Rabat (
    idRabat SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    wielkosc NUMERIC(5, 2),
    FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
    FOREIGN KEY (idAgent) REFERENCES Agent(idAgent)
);

-- Inserting records into Discount (Rabat) table
-- Wstawianie rekordów do tabeli Rabat
INSERT INTO Rabat (idKlient, idAgent, wielkosc) VALUES
    (1, 1, 10.00),
    (2, 2, 15.00),
    (3, 3, 20.00),
    (4, 4, 25.00),
    (5, 5, 30.00);

-- Creating table Uslugi (Services)
-- Tworzenie tabeli Uslugi
CREATE TABLE Uslugi (
    idUslugi SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    nazwa VARCHAR(100) NOT NULL,
    koszt NUMERIC(15, 2) NOT NULL,
    termin DATE,
    FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
    FOREIGN KEY (idAgent) REFERENCES Agent(idAgent)
);

-- Inserting records into Uslugi (Services) table
-- Wstawianie rekordów do tabeli Uslugi
INSERT INTO Uslugi (idKlient, idAgent, nazwa, koszt, termin) VALUES
    (1, 1, 'Serwis samochodu', 100.00, '2024-05-15'),
    (2, 2, 'Naprawa dachu', 200.00, '2024-06-10'),
    (3, 3, 'Badanie lekarskie', 150.00, '2024-05-20'),
    (4, 4, 'Przygotowanie dokumentów', 300.00, '2024-05-25'),
    (5, 5, 'Doradztwo biznesowe', 500.00, '2024-06-01');