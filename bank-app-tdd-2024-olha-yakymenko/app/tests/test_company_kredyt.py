import unittest
from parameterized import parameterized
from ..CompanyAccount import CompanyAccount
from unittest.mock import patch

class TestCompanyKredyt(unittest.TestCase):
    nazwa="Nazwa"
    nip="8461627563"

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def setUp(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = True
        self.konto = CompanyAccount(self.nazwa, self.nip)

    @parameterized.expand([
        ([-100, 200, -33, 10, 200, 50], 777, 1000, 777, "Saldo powinno pozostać bez zmian, bo kredyt przekracza dostępny limit"),
        ([-100, 200, -33, 10, 200, 1775], 5000, 1000, 6000, "Saldo powinno wzrosnąć o kwotę kredytu"),
        ([-100, 200, -33, 10, 200], 5000, 1000, 5000, "Saldo nie powinno się zmienić, bo kredyt przekracza dostępny limit"),
        ([-100, 200, 1775, -33, 10, 200], 2000, 1000, 3000, "Saldo powinno wzrosnąć o kwotę kredytu w ramach limitu"),
        ([-100, 200, -33, 10, 200, 1775], 5000, 10000, 5000, "Saldo nie powinno się zmienić, bo kredyt przekracza dostępny limit"),
        ([], 1000, 200, 1000, "Saldo nie powinno się zmienić przy braku historii operacji"),
        ([-100, 200, -33, 10, 200, 1775], 1000, 0, 1000, "Saldo nie powinno się zmienić przy braku wnioskowanego kredytu"),
    ])
    
    def test_personal_kredyt(self, historia, saldo, kredyt, expected, description):
        with self.subTest(description=description):
            self.konto.historia = historia
            self.konto.saldo = saldo
            self.konto.zaciagnij_kredyt(kredyt)
            self.assertEqual(self.konto.saldo, expected, description)
