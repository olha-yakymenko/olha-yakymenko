import unittest
from unittest.mock import patch
from parameterized import parameterized
from app.PersonalAccount import PersonalAccount

class TestPersonalKredyt(unittest.TestCase):
    imie = "Dariusz"
    nazwisko = "Januszewski"
    pesel = "06211888888"

    def setUp(self):
        self.konto = PersonalAccount(self.imie, self.nazwisko, self.pesel)

    @parameterized.expand([
        ([-100, 200, -33, 10, 200, 50], 1000, True, 1000),
        ([-100, 200, -5000, 1, -2, -3], 1000, False, 0),
        ([-100, 200, 5000, 1, -2, -3], 1000, True, 1000),
        ([-100, 200, 500, 100, -1, 300], 10000, False, 0),
        ([200, 500], 10000, False, 0),
        ([-666, 200000, 500, -1, 200], 10000, True, 10000),
        ([], 1000, False, 0),
        ([100, -200, 300], 1000, False, 0),
        ([100, 200, 300, 400, 500], 1000, True, 1000),
    ])
    def test_personal_kredyt(self, historia, kredyt, expected, expected_saldo):
        with patch('app.BlackList.BlackList.is_account_on_black_list') as mock_is_account_blocked:
            mock_is_account_blocked.return_value = False
            self.konto.historia = historia
            czy_przyznany = self.konto.zaciagnij_kredyt(kredyt)
            self.assertEqual(czy_przyznany, expected)
            self.assertEqual(self.konto.saldo, expected_saldo, "Saldo nie zostało zwiększone")

    @patch('app.BlackList.BlackList.is_account_on_black_list')
    def test_loan_blocked(self, mock_is_account_on_black_list):
        mock_is_account_on_black_list.return_value = True
        self.konto.historia = [100, 100, 100]
        self.assertFalse(self.konto.zaciagnij_kredyt(100), "Kredyt został przyznany mimo blokady")
