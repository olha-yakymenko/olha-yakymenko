import unittest
from parameterized import parameterized
from ..PersonalAccount import PersonalAccount

class TestCreateBankAccount(unittest.TestCase):
    imie = "Dariusz"
    nazwisko = "Januszewski"
    pesel = "06211888888"

    def setUp(self):
        self.konto = PersonalAccount(self.imie, self.nazwisko, self.pesel)

    
    @parameterized.expand([
        ("test tworzenia konta", "Dariusz", "Januszewski", "06211888888", 0, []),
    ])
    def test_tworzenie_konta(self, name, imie, nazwisko, pesel, saldo, historia):
        konto = PersonalAccount(imie, nazwisko, pesel)  
        self.assertEqual(konto.imie, imie, f"{name} - imie nie zostało zapisane!")
        self.assertEqual(konto.nazwisko, nazwisko, f"{name} - nazwisko nie zostało zapisane!")
        self.assertEqual(konto.saldo, saldo, f"{name} - saldo nie zostało ustawione na 0!")
        self.assertEqual(konto.pesel, pesel, f"{name} - pesel nie został zapisany!")
        self.assertEqual(konto.historia, historia, f"{name} - historia transakcji nie jest pusta!")

    @parameterized.expand([
        ("test za krótki pesel", "123", "Niepoprawny pesel"),
        ("test za długi pesel", "123878669696966969", "Niepoprawny pesel"),
    ])
    def test_pesel(self, name, pesel, expected_result):
        konto = PersonalAccount(self.imie, self.nazwisko, pesel)
        self.assertEqual(konto.pesel, expected_result, f"{name} - pesel nie został zapisany")

    @parameterized.expand([
        ("test zły kod(dlugi), dobry rok", "Prombkjjgkghgg", "6105158888", 0),
        ("test zły kod(krotki), dobry rok", "Pro", "6105158888", 0),
        ("test dobry kod, zły rok", "PROM_123", "5905158888", 0),
        ("test dobry kod, dobry rok", "PROM_123", "6105158888", 50),
        ("test zły kod i rok", "PROM_876", "5905158888", 0),
        ("test zły kod (niepoprawny) i dobry rok", "AAA_876", "6105158888", 0),
        ("test brak kodu promocyjnego", None, "6105158888", 0),
    ])
    def test_kod_promocyjny(self, name, kod, pesel, expected_saldo):
        konto = PersonalAccount(self.imie, self.nazwisko, pesel, kod)
        self.assertEqual(konto.saldo, expected_saldo, f"{name} - niepoprawne saldo")
