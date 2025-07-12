INSERT INTO Uslugi (idKlient, idAgent, nazwa, koszt, termin) VALUES
    (1, 1, 'Serwis samochodu', 100.00, '2024-05-15'),
    (2, 2, 'Naprawa dachu', 200.00, '2024-06-10'),
    (3, 3, 'Badanie lekarskie', 150.00, '2024-05-20'),
    (4, 4, 'Przygotowanie dokumentów', 300.00, '2024-05-25'),
    (5, 5, 'Doradztwo biznesowe', 500.00, '2024-06-01');
    
INSERT INTO Wyplata (idZdarzenie, data_zgloszenia, kwota, idOsrodek_ubezpieczeniowy, data_wyplaty, sposob_wyplaty) VALUES
    (1, '2024-05-02', 200.00, 1, '2024-05-10', 'Przelew'),
    (2, '2024-05-06', 300.00, 2, '2024-05-12', 'Gotówka'),
    (3, '2024-05-11', 400.00, 3, '2024-05-18', 'Przelew'),
    (4, '2024-05-16', 500.00, 4, '2024-05-24', 'Przelew'),
    (5, '2024-05-21', 600.00, 5, '2024-05-28', 'Gotówka');

INSERT INTO Zdarzenie (idKlient, data_zgloszenia, typ, status_obslugi) VALUES
    (1, '2024-05-01', 'Awaria pojazdu', 'W trakcie obsługi'),
    (2, '2024-05-05', 'Uszkodzenie mienia', 'Oczekujące na decyzję'),
    (3, '2024-05-10', 'Wizyta lekarska', 'Zakończone'),
    (4, '2024-05-15', 'Zgłoszenie śmierci', 'W trakcie obsługi'),
    (5, '2024-05-20', 'Rozpoczęcie działalności', 'Nowe zgłoszenie');

INSERT INTO Rodzaj_ubezpieczenia (idPolis, nazwa, opis) VALUES
    (1, 'OC', 'Ubezpieczenie od odpowiedzialności cywilnej'),
    (2, 'NNW', 'Ubezpieczenie następstw nieszczęśliwych wypadków'),
    (3, 'NFZ', 'Ubezpieczenie finansowane przez Narodowy Fundusz Zdrowia'),
    (4, 'Śmierć', 'Ubezpieczenie na wypadek śmierci'),
    (5, 'OC', 'Ubezpieczenie od odpowiedzialności cywilnej');

INSERT INTO Polis (idKlient, idAgent, nazwa, opis, cena) VALUES
    (1, 1, 'Polisa komunikacyjna', 'Polisa ubezpieczenia samochodu', 500.00),
    (2, 2, 'Polisa mieszkania', 'Polisa ubezpieczenia domu', 800.00),
    (3, 3, 'Polisa zdrowotna', 'Polisa ubezpieczenia zdrowotnego', 1200.00),
    (4, 4, 'Polisa na życie', 'Polisa ubezpieczenia na życie', 1500.00),
    (5, 5, 'Polisa firmowa', 'Polisa ubezpieczenia działalności gospodarczej', 2000.00);

INSERT INTO Agent (imie, nazwisko, numer_pesel, stanowisko, idosrodek_ubezpieczeniowy) VALUES
    ('Adam', 'Kowalski', '80010112345', 'Agent ubezpieczeniowy', 1),
    ('Anna', 'Nowak', '81020223456', 'Agent ubezpieczeniowy', 2),
    ('Piotr', 'Wiśniewski', '82030334567', 'Agent ubezpieczeniowy', 3),
    ('Magda', 'Kaczmarek', '83040445678', 'Agent ubezpieczeniowy', 4),
    ('Tomasz', 'Lewandowski', '84050556789', 'Agent ubezpieczeniowy', 5);

INSERT INTO Przedmiot_ubezpieczenia (nazwa, opis, idKlient, idWycena) VALUES
    ('Samochód', 'Ubezpieczenie komunikacyjne', 1, 1),
    ('Dom', 'Ubezpieczenie mieszkania', 2, 2),
    ('Zdrowie', 'Ubezpieczenie zdrowotne', 3, 3),
    ('Życie', 'Ubezpieczenie na życie', 4, 4),
    ('Firma', 'Ubezpieczenie działalności gospodarczej', 5, 5);


INSERT INTO Wycena (data_wyceny, kwota, opis) VALUES
    ('2024-05-01', 500.00, 'Wycena ubezpieczenia samochodu'),
    ('2024-05-05', 700.00, 'Wycena ubezpieczenia mieszkania'),
    ('2024-05-10', 1000.00, 'Wycena ubezpieczenia na życie'),
    ('2024-05-15', 1200.00, 'Wycena ubezpieczenia działalności gospodarczej'),
    ('2024-05-20', 1500.00, 'Wycena ubezpieczenia zdrowotnego');


INSERT INTO Kontakt (numer_telefonu, email) VALUES
    ('123456789', 'john.doe@example.com'),
    ('987654321', 'jane.smith@example.com'),
    ('111222333', 'michael.johnson@example.com'),
    ('444555666', 'emily.williams@example.com'),
    ('777888999', 'daniel.brown@example.com');

INSERT INTO Klient (imie, nazwisko, data_urodzenia, pesel, idAdres) VALUES
    ('John', 'Doe', '1985-03-15', '85031512345', 1),
    ('Jane', 'Smith', '1990-07-20', '90072098765', 2),
    ('Michael', 'Johnson', '1976-11-10', '76111054321', 3),
    ('Emily', 'Williams', '1988-05-03', '88050365432', 4),
    ('Daniel', 'Brown', '1995-09-25', '95092578901', 5);

INSERT INTO Adres (ulica, numer_domu, kod_pocztowy, miasto) VALUES
    ('Main Street', '123', '12345', 'Anytown'),
    ('Oak Avenue', '456', '54321', 'Otherville'),
    ('Elm Road', '789', '98765', 'Mapleton'),
    ('Pine Lane', '101', '13579', 'Springside'),
    ('Maple Drive', '202', '24680', 'Riverside');

INSERT INTO Osrodek_ubezpieczeniowy (idAdres, idKontakt) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);
INSERT INTO Rabat (idKlient, idAgent, wielkosc) VALUES
    (1, 1, 10.00),
    (2, 2, 15.00),
    (3, 3, 20.00),
    (4, 4, 25.00),
    (5, 5, 30.00);



