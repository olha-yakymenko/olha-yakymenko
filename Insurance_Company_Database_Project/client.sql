CREATE TABLE Klient (
    idKlient SERIAL PRIMARY KEY,
    imie VARCHAR(50),
    nazwisko VARCHAR(50)
);

INSERT INTO Klient (imie, nazwisko) VALUES
('Jan', 'Kowalski'),
('Anna', 'Nowak'),
('Piotr', 'Wiśniewski');

CREATE OR REPLACE FUNCTION clients_cursor()
RETURNS TABLE (idKlient INT, imie VARCHAR, nazwisko VARCHAR) AS $$
DECLARE
    rec Klient; 
    cur CURSOR FOR SELECT * FROM Klient;  
BEGIN
    OPEN cur;
    LOOP
        FETCH cur INTO rec;
        EXIT WHEN NOT FOUND;
        idKlient := rec.idKlient;
        imie := rec.imie;
        nazwisko := rec.nazwisko;
        RETURN NEXT;
    END LOOP;
    CLOSE cur;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM clients_cursor();
