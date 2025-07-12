CREATE TABLE Adres (
    idAdres SERIAL PRIMARY KEY,
    ulica VARCHAR(100),
    numer_domu VARCHAR(10),
    kod_pocztowy VARCHAR(10),
    miasto VARCHAR(50)
);



CREATE TABLE Klient (
    idKlient SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    data_urodzenia DATE,
    pesel VARCHAR(11) UNIQUE,
    idAdres INT NOT NULL
);



CREATE TABLE Kontakt (
    idKontakt SERIAL PRIMARY KEY,
    numer_telefonu VARCHAR(15),
    email VARCHAR(50)
);




CREATE TABLE Osrodek_ubezpieczeniowy (
    idOsrodek_ubezpieczeniowy SERIAL PRIMARY KEY,
    idAdres INT NOT NULL,
    idKontakt INT NOT NULL
);




CREATE TABLE Wycena (
    idWycena SERIAL PRIMARY KEY,
    data_wyceny DATE NOT NULL,
    kwota NUMERIC(15, 2) NOT NULL,
    opis TEXT
);




CREATE TABLE Przedmiot_ubezpieczenia (
    idPrzedmiot_ubezpieczenia SERIAL PRIMARY KEY,
    nazwa VARCHAR(100) NOT NULL,
    opis TEXT,
    idKlient INT NOT NULL,
    idWycena INT NOT NULL
 );





CREATE TABLE Agent (
    idAgent SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL,
    numer_pesel VARCHAR(11) UNIQUE,
    stanowisko VARCHAR(50) NOT NULL,
    idosrodek_ubezpieczeniowy INT NOT NULL
    
);



CREATE TABLE Polis (
    idPolis SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    nazwa VARCHAR(50) NOT NULL,
    opis TEXT NOT NULL,
    cena NUMERIC(15, 2) NOT NULL
   
);


CREATE TABLE Rodzaj_ubezpieczenia (
    idRodzaj_ubezpieczenia SERIAL PRIMARY KEY,
    idPolis INT NOT NULL,
    nazwa VARCHAR(100),
    opis TEXT
    
);




CREATE TABLE Zdarzenie (
    idZdarzenie SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    data_zgloszenia DATE NOT NULL,
    typ VARCHAR(50),
    status_obslugi VARCHAR(100)
    
);




CREATE TABLE Wyplata (
    idWyplata SERIAL PRIMARY KEY,
    idZdarzenie INT NOT NULL,
    data_zgloszenia DATE NOT NULL,
    kwota NUMERIC(15, 2) NOT NULL,
    idOsrodek_ubezpieczeniowy INT NOT NULL,
    data_wyplaty DATE NOT NULL,
    sposob_wyplaty VARCHAR(50) NOT NULL
    
);



CREATE TABLE Rabat (
    idRabat SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    wielkosc NUMERIC(5, 2)
   
);


CREATE TABLE Uslugi (
    idUslugi SERIAL PRIMARY KEY,
    idKlient INT NOT NULL,
    idAgent INT NOT NULL,
    nazwa VARCHAR(100) NOT NULL,
    koszt NUMERIC(15, 2) NOT NULL,
    termin DATE
    
);

--ALTER TABLE Klient 
--ADD FOREIGN KEY (idAdres) REFERENCES Adres(idAdres);

--ALTER TABLE Osrodek_ubezpieczeniowy 
--ADD FOREIGN KEY (idAdres) REFERENCES Adres(idAdres),
--ADD FOREIGN KEY (idKontakt) REFERENCES Kontakt(idKontakt);

--ALTER TABLE Przedmiot_ubezpieczenia 
--ADD FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
--ADD FOREIGN KEY (idWycena) REFERENCES Wycena(idWycena);

--ALTER TABLE Agent 
--ADD FOREIGN KEY (idosrodek_ubezpieczeniowy) REFERENCES Osrodek_ubezpieczeniowy(idOsrodek_ubezpieczeniowy);

--ALTER TABLE Polis 
--ADD FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
--ADD FOREIGN KEY (idAgent) REFERENCES Agent(idAgent);

--ALTER TABLE Rodzaj_ubezpieczenia 
--ADD FOREIGN KEY (idPolis) REFERENCES Polis(idPolis);

--ALTER TABLE Zdarzenie 
--ADD FOREIGN KEY (idKlient) REFERENCES Klient(idKlient);

--ALTER TABLE Wyplata 
--ADD FOREIGN KEY (idZdarzenie) REFERENCES Zdarzenie(idZdarzenie),
--ADD FOREIGN KEY (idOsrodek_ubezpieczeniowy) REFERENCES Osrodek_ubezpieczeniowy(idOsrodek_ubezpieczeniowy);

--ALTER TABLE Rabat 
--ADD FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
--ADD FOREIGN KEY (idAgent) REFERENCES Agent(idAgent);

--ALTER TABLE Uslugi 
--ADD FOREIGN KEY (idKlient) REFERENCES Klient(idKlient),
--ADD FOREIGN KEY (idAgent) REFERENCES Agent(idAgent);


