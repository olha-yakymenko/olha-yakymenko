CREATE OR REPLACE FUNCTION clients_cursor()
RETURNS TABLE (idKlient INT, imie VARCHAR, nazwisko VARCHAR) AS $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN SELECT idKlient, imie, nazwisko FROM Klient LOOP
        RETURN NEXT rec;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM clients_cursor();
