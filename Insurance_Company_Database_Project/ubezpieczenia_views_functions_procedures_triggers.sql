-- Tworzenie widoków
CREATE VIEW Wyceny_Klientow AS
SELECT k.imie, k.nazwisko, w.data_wyceny, w.kwota, w.opis
FROM Klient k
JOIN Przedmiot_ubezpieczenia pu ON k.idKlient = pu.idKlient
JOIN Wycena w ON pu.idWycena = w.idWycena;
--drop view Wyceny_klientow;

CREATE VIEW Zgloszenia_Klientow AS
SELECT k.imie, k.nazwisko, z.data_zgloszenia, z.typ, z.status_obslugi
FROM Klient k
JOIN Zdarzenie z ON k.idKlient = z.idKlient;
--drop view zgloszenia_klientow;

CREATE VIEW Wyplaty_Osrodkow AS
SELECT o.idOsrodek_ubezpieczeniowy, o.idAdres, o.idKontakt, w.data_zgloszenia, w.kwota, w.sposob_wyplaty
FROM Osrodek_ubezpieczeniowy o
JOIN Wyplata w ON o.idOsrodek_ubezpieczeniowy = w.idOsrodek_ubezpieczeniowy;
--drop view Wyplaty_Osrodkow;

-- Sprawdzenie widoków
SELECT * FROM Wyceny_Klientow;
SELECT * FROM Zgloszenia_Klientow;
SELECT * FROM Wyplaty_Osrodkow;

-- Tworzenie funkcji
CREATE OR REPLACE FUNCTION ObliczWysokoscRabatu(p_klient_id INT, p_agent_id INT) 
RETURNS NUMERIC AS $$
DECLARE
    rabat NUMERIC;
BEGIN
    SELECT wielkosc INTO rabat
    FROM Rabat
    WHERE idKlient = p_klient_id AND idAgent = p_agent_id;
    
    IF rabat IS NULL THEN
        RETURN 0;
    ELSE
        RETURN rabat;
    END IF;
END;
$$ LANGUAGE plpgsql;
--drop FUNCTION ObliczWysokoscRabatu;

CREATE OR REPLACE FUNCTION ObliczKosztUbezpieczenia(p_wycena_id INT, p_ilosc_przedmiotow INT) 
RETURNS NUMERIC AS $$
DECLARE
    koszt NUMERIC;
BEGIN
    SELECT kwota INTO koszt
    FROM Wycena
    WHERE idWycena = p_wycena_id;
    
    RETURN koszt * p_ilosc_przedmiotow;
END;
$$ LANGUAGE plpgsql;
--drop FUNCTION ObliczKosztUbezpieczenia;

-- Sprawdzenie funkcji
SELECT ObliczWysokoscRabatu(1, 1); -- Przykładowe wartości klient_id i agent_id
SELECT ObliczKosztUbezpieczenia(1, 3); -- Przykładowe wartości wycena_id i ilosc_przedmiotow

-- Tworzenie procedur
CREATE OR REPLACE PROCEDURE DodajZgloszenie(p_klient_id INT, p_data_zgloszenia DATE, p_typ VARCHAR(50), p_status_obslugi VARCHAR(100))
AS $$
BEGIN
    INSERT INTO Zdarzenie (idKlient, data_zgloszenia, typ, status_obslugi)
    VALUES (p_klient_id, p_data_zgloszenia, p_typ, p_status_obslugi);
END;
$$ LANGUAGE plpgsql;
--drop PROCEDURE DodajZgloszenie;

CREATE OR REPLACE PROCEDURE ZmienStatusZgloszenia(p_zdarzenie_id INT, p_nowy_status VARCHAR(100))
AS $$
BEGIN
    UPDATE Zdarzenie
    SET status_obslugi = p_nowy_status
    WHERE idZdarzenie = p_zdarzenie_id;
END;
$$ LANGUAGE plpgsql;
--drop PROCEDURE zmienstatuszgloszenia;

-- Sprawdzenie procedur
CALL DodajZgloszenie(1, '2024-05-24', 'Awaria', 'Oczekujące'); -- Przykładowe wartości parametrów
CALL ZmienStatusZgloszenia(1, 'W trakcie naprawy'); -- Przykładowe wartości parametrów

-- Tworzenie wyzwalaczy
CREATE OR REPLACE FUNCTION AktualizujCenePolisy()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Polis
    SET cena = NEW.cena
    WHERE idPolis = NEW.idPolis;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER PolisaCenaUpdate
AFTER UPDATE ON Polis
FOR EACH ROW
EXECUTE FUNCTION AktualizujCenePolisy();

CREATE OR REPLACE FUNCTION AktualizujStatusZgloszenia()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Zdarzenie
    SET status_obslugi = 'Zaakceptowane'
    WHERE idZdarzenie = NEW.idZdarzenie;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ZgloszenieStatusUpdate
AFTER INSERT ON Zdarzenie
FOR EACH ROW
WHEN (NEW.status_obslugi = 'Oczekujące')
EXECUTE FUNCTION AktualizujStatusZgloszenia();

-- Sprawdzenie wyzwalaczy: Wymaga ręcznego sprawdzenia w bazie danych po wykonaniu odpowiednich operacji

-- Tworzenie wyzwalacza 3
CREATE OR REPLACE FUNCTION SprawdzPrzekroczenieWartosci()
RETURNS TRIGGER AS $$
BEGIN
    DECLARE
        wycena_kwota NUMERIC;
    BEGIN
        SELECT kwota INTO wycena_kwota
        FROM Wycena
        WHERE idWycena = NEW.idWycena;
        
        IF NEW.kwota > wycena_kwota THEN
            RAISE EXCEPTION 'Kwota polisy przekracza wartość wyceny';
        END IF;
    END;
END;
$$ LANGUAGE plpgsql;

-- Dodawanie wyzwalacza 3 do tabeli Polis
CREATE TRIGGER PrzekroczenieWartosciTrigger
BEFORE INSERT ON Polis
FOR EACH ROW
EXECUTE FUNCTION SprawdzPrzekroczenieWartosci();
