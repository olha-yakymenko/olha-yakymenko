import unittest
from parameterized import parameterized
from unittest.mock import patch

from ..PersonalAccount import PersonalAccount
from ..CompanyAccount import CompanyAccount

class TestTransferPersonal(unittest.TestCase):
    imie = "Dariusz"
    nazwisko = "Januszewski"
    pesel = "06211888888"

    def setUp(self):
        self.konto = PersonalAccount(self.imie, self.nazwisko, self.pesel)


    @parameterized.expand([
        ("przelew wychodzący - poprawnie", 1000, 100, 900),
        ("przelew wychodzący - za mało środków", 50, 100, 50),
    ])
    def test_przelew_wychodzacy(self, name, saldo_start, przelew_kwota, saldo_end):
        self.konto.saldo = saldo_start
        self.konto.przelew_wychodzacy(przelew_kwota)
        self.assertEqual(self.konto.saldo, saldo_end, f"{name} - saldo niepoprawne po przelewie wychodzącym")

    def test_przelew_przychodzacy(self):
        self.konto.saldo = 1000
        self.konto.przelew_przychodzacy(100)
        self.assertEqual(self.konto.saldo, 1100)

    @parameterized.expand([
        ("szybki przelew - za mało środków", 110, 150, 110),
        ("szybki przelew - wystarczająca kwota", 161, 160, 0),
        ("szybki przelew - saldo poniżej 0", 160, 160, -1),
    ])
    def test_szybki_przelew(self, name, saldo_start, przelew_kwota, saldo_end):
        self.konto.saldo = saldo_start
        self.konto.szybki_przelew(przelew_kwota)
        self.assertEqual(self.konto.saldo, saldo_end, f"{name} - saldo niepoprawne po szybkim przelewie")

    def test_kilka_przelewow_dobrze(self):
        self.konto.saldo=150
        self.konto.szybki_przelew(50)
        self.konto.przelew_przychodzacy(10)
        self.konto.przelew_wychodzacy(30)
        self.assertEqual(self.konto.saldo, 150-50-1+10-30)

    def test_historia_dobrze(self):
        self.konto.saldo = 1000
        self.konto.przelew_wychodzacy(100)
        self.konto.przelew_przychodzacy(200)
        self.konto.szybki_przelew(100)
        self.assertEqual(self.konto.historia, [-100, 200, -100, -1])



class TestTransferCompany(unittest.TestCase):
    nazwa="FIRMA"
    nip="8461627563"

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def setUp(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = True
        self.konto = CompanyAccount(self.nazwa, self.nip)

    @parameterized.expand([
        ("przelew wychodzący - poprawnie", 1000, 100, 900),
        ("przelew wychodzący - za mało środków", 50, 100, 50),
    ])
    def test_przelew_wychodzacy(self, name, saldo_start, przelew_kwota, saldo_end):
        self.konto.saldo = saldo_start
        self.konto.przelew_wychodzacy(przelew_kwota)
        self.assertEqual(self.konto.saldo, saldo_end, f"{name} - saldo niepoprawne po przelewie wychodzącym")

    def test_przelew_przychodzacy_dobrze(self):
        self.konto.saldo = 1000
        self.konto.przelew_przychodzacy(100)
        self.assertEqual(self.konto.saldo, 1100)

    @parameterized.expand([
        ("szybki przelew - za mało środków", 110, 150, 110),
        ("szybki przelew - wystarczająca kwota", 160, 150, 5),
        ("szybki przelew - saldo poniżej 0", 160, 160, -5),
    ])
    def test_szybki_przelew(self, name, saldo_start, przelew_kwota, saldo_end):
        self.konto.saldo = saldo_start
        self.konto.szybki_przelew(przelew_kwota)
        self.assertEqual(self.konto.saldo, saldo_end, f"{name} - saldo niepoprawne po szybkim przelewie")

    def test_kilka_przelewow_dobrze(self):
        self.konto.saldo=150
        self.konto.szybki_przelew(50)
        self.konto.przelew_przychodzacy(10)
        self.konto.przelew_wychodzacy(30)
        self.assertEqual(self.konto.saldo, 150-50-5+10-30)

    def test_historia_dobrze(self):
        self.konto.saldo = 1000
        self.konto.przelew_wychodzacy(100)
        self.konto.przelew_przychodzacy(200)
        self.konto.szybki_przelew(100)
        self.assertEqual(self.konto.historia, [-100, 200, -100, -5])