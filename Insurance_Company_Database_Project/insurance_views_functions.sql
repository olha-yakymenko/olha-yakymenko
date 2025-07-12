
--1a)
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


--1b)
SELECT * FROM Wyceny_Klientow;
SELECT * FROM Zgloszenia_Klientow;
SELECT * FROM Wyplaty_Osrodkow;

--2a)
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

--2b)
SELECT ObliczWysokoscRabatu(1, 1);

--3a)
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

--3b)
SELECT ObliczKosztUbezpieczenia(1, 3);

--4a)
CREATE OR REPLACE PROCEDURE DodajZgloszenie(p_klient_id INT, p_data_zgloszenia DATE, p_typ VARCHAR(50), p_status_obslugi VARCHAR(100))
AS $$
BEGIN
    INSERT INTO Zdarzenie (idKlient, data_zgloszenia, typ, status_obslugi)
    VALUES (p_klient_id, p_data_zgloszenia, p_typ, p_status_obslugi);
END;
$$ LANGUAGE plpgsql;
--drop PROCEDURE DodajZgloszenie;

--4b)
CALL DodajZgloszenie(1,  '2024-06-24', 'Awaria', 'Oczekujące');
select * from Zdarzenie;


--5a)
CREATE OR REPLACE PROCEDURE ZmienStatusZgloszenia(p_zdarzenie_id INT, p_nowy_status VARCHAR(100))
AS $$
BEGIN
    UPDATE Zdarzenie
    SET status_obslugi = p_nowy_status
    WHERE idZdarzenie = p_zdarzenie_id;
END;
$$ LANGUAGE plpgsql;
--drop PROCEDURE zmienstatuszgloszenia;

--5b)
CALL ZmienStatusZgloszenia(1, 'W trakcie naprawy'); -- Przykładowe wartości parametrów
select * from zdarzenie;

--6a)
CREATE OR REPLACE FUNCTION auto_set_wycena_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_wyceny := CURRENT_DATE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_wycena_date
BEFORE INSERT ON Wycena
FOR EACH ROW
EXECUTE FUNCTION auto_set_wycena_date();
--DROP FUNCTION IF EXISTS auto_set_wycena_date() CASCADE;
--drop FUNCTION if EXISTS set_wycena_date();

--6b)
INSERT INTO Wycena (kwota, opis) VALUES (1000.00, 'Pierwsza wycena');
SELECT * FROM Wycena WHERE opis = 'Pierwsza wycena';

--7a)
CREATE OR REPLACE FUNCTION check_pesel_format()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.pesel !~ '^[0-9]{11}$' THEN
        RAISE EXCEPTION 'PESEL musi zawierac 11 liczb';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER validate_pesel_format
AFTER INSERT OR UPDATE ON Klient
FOR EACH ROW
EXECUTE FUNCTION check_pesel_format();
--DROP FUNCTION IF EXISTS check_pesel_format() CASCADE;
--DROP FUNCTION IF EXISTS validate_pesel_format() CASCADE;

--7b)
INSERT INTO Klient (imie, nazwisko, data_urodzenia, pesel, idAdres)
VALUES ('Jan', 'Kowalski', '1980-01-01', '12345678901', 1);

INSERT INTO Klient (imie, nazwisko, data_urodzenia, pesel, idAdres)
VALUES ('Anna', 'Nowak', '1990-02-02', '1234567890A', 1);


--8a)
CREATE OR REPLACE FUNCTION calculate_discount()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.koszt >= 1000 THEN
        INSERT INTO Rabat (idKlient, idAgent, wielkosc)
        VALUES (NEW.idKlient, NEW.idAgent, NEW.koszt * 0.1); -- 10% rabatu na podstawie kosztu usługi
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER apply_discount
AFTER INSERT ON Uslugi
FOR EACH ROW
EXECUTE FUNCTION calculate_discount();
--DROP FUNCTION IF EXISTS calculate_discount() CASCADE;
--drop FUNCTION if EXISTS apply_discount() CASCADE;

--8b)
INSERT INTO Uslugi (idKlient, idAgent, nazwa, koszt, termin)
VALUES (1, 1, 'Usługa Premium', 1500.00, '2024-07-01');

INSERT INTO Uslugi (idKlient, idAgent, nazwa, koszt, termin)
VALUES (1, 1, 'Usługa Standard', 500.00, '2024-07-01');

SELECT * FROM Rabat WHERE idKlient = 1;

--9a)
CREATE OR REPLACE FUNCTION set_rodzaj_ubezpieczenia_description()
RETURNS TRIGGER AS $$
BEGIN
    NEW.opis := 'Opis dla rodzaju ubezpieczenia: ' || NEW.nazwa;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER set_rodzaj_ubezpieczenia_description_trigger
BEFORE INSERT ON Rodzaj_ubezpieczenia
FOR EACH ROW
EXECUTE FUNCTION set_rodzaj_ubezpieczenia_description();


--DROP FUNCTION if EXISTS set_rodzaj_ubezpieczenia_description() CASCADE;
--DROP FUNCTION IF EXISTS set_rodzaj_ubezpieczenia_description_trigger() CASCADE;

--9b)
INSERT INTO Rodzaj_ubezpieczenia (nazwa, idpolis)
VALUES ('Ubezpieczenie zdrowotne',  1);
select * from rodzaj_ubezpieczenia;


--10a)

CREATE OR REPLACE FUNCTION fetch_clients_and_contacts()
RETURNS TABLE (
    idKlient INT,
    imie VARCHAR(50),
    nazwisko VARCHAR(50),
    data_urodzenia DATE,
    pesel VARCHAR(11),
    ulica VARCHAR(100),
    numer_domu VARCHAR(10),
    kod_pocztowy VARCHAR(10),
    miasto VARCHAR(50)
) AS $$
BEGIN
    RETURN QUERY
        SELECT 
            k.idKlient, k.imie, k.nazwisko, k.data_urodzenia, k.pesel,
            a.ulica, a.numer_domu, a.kod_pocztowy, a.miasto
        FROM 
            Klient k
        JOIN 
            Adres a ON k.idAdres = a.idAdres;
END;
$$ LANGUAGE PLPGSQL;
--DROP FUNCTION IF EXISTS fetch_clients_and_contacts();

CREATE OR REPLACE FUNCTION fetch_policies_and_agents()
RETURNS TABLE (
    idPolis INT,
    nazwa_polisy VARCHAR(50),
    opis_polisy TEXT,
    cena NUMERIC(15, 2),
    idRodzaj_ubezpieczenia INT,
    nazwa_rodzaju VARCHAR(100),
    opis_rodzaju TEXT,
    idAgent INT,
    imie_agenta VARCHAR(50),
    nazwisko_agenta VARCHAR(50),
    stanowisko VARCHAR(50)
) AS $$
BEGIN
    RETURN QUERY
        SELECT 
            p.idPolis, p.nazwa AS nazwa_polisy, p.opis AS opis_polisy, p.cena,
            r.idRodzaj_ubezpieczenia, r.nazwa AS nazwa_rodzaju, r.opis AS opis_rodzaju,
            a.idAgent, a.imie AS imie_agenta, a.nazwisko AS nazwisko_agenta, a.stanowisko
        FROM 
            Polis p
        JOIN 
            Agent a ON p.idAgent = a.idAgent
        LEFT JOIN 
            Rodzaj_ubezpieczenia r ON p.idPolis = r.idPolis;
END;
$$ LANGUAGE PLPGSQL;
--DROP FUNCTION IF EXISTS fetch_policies_and_agents();

--10b)
SELECT * FROM fetch_policies_and_agents();


