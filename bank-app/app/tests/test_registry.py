import unittest
from parameterized import parameterized

from ..PersonalAccount import PersonalAccount
from ..AccountRegistry import AccountRegistry
imie = "Dariusz"
nazwisko = "Januszewski"
pesel = "06211888888"
pesel_66 = "89394029495"
pesel_77 = "893940739495"
konto = PersonalAccount(imie, nazwisko, pesel)


class TestRegistry(unittest.TestCase):
    imie = "Dariusz"
    nazwisko = "Januszewski"
    pesel = "06211888888"
    pesel_1 = "89394029495"
    pesel_2 = "893940739495"
    konto = PersonalAccount(imie, nazwisko, pesel)
    konto1 = PersonalAccount(imie, nazwisko, pesel_1)
    konto2 = PersonalAccount(imie, nazwisko, pesel_2)

    def setUp(self):
        AccountRegistry.registry = []  

    @parameterized.expand([
        ("single_account", [konto], 1),
        ("multiple_accounts", [
            konto1,
            konto2
        ], 2),
    ])
    def test_add_accounts(self, name, accounts_to_add, expected_count):
        for account in accounts_to_add:
            AccountRegistry.add_account(account)
        self.assertEqual(AccountRegistry.get_accounts_count(), expected_count, f"Niepoprawna liczba kont: {name}")



    @parameterized.expand([
        ("single_account", [konto], "06211888888", konto),
        ("multiple_accounts", [
            konto1,
            konto2
        ], "89394029495", konto1),
        ("nonexistent_account", [
            konto1, konto2
        ], "43546478489", None),
    ])
    def test_search_by_pesel(self, name, accounts_to_add, searching_pesel, expected_account):
        for account in accounts_to_add:
            AccountRegistry.add_account(account)
        found_account = AccountRegistry.search_by_pesel(searching_pesel)
        if expected_account is None:
            self.assertEqual(found_account, None, f"Niepoprawny wynik wyszukiwania: {name}")
        else:
            self.assertEqual(found_account.imie, expected_account.imie, f"Niepoprawne imie: {name}")
            self.assertEqual(found_account.nazwisko, expected_account.nazwisko, f"Niepoprawne nazwisko: {name}")
            self.assertEqual(found_account.pesel, expected_account.pesel, f"Niepoprawny pesel: {name}")

    @parameterized.expand([
        ("delete_existing_account", [konto], "06211888888"),
       ("delete_nonexistent_account", [konto], "43546478489"),
    ])
    def test_delete_account(self, name, accounts_to_add, deleting_pesel):
        for account in accounts_to_add:
            AccountRegistry.add_account(account)
        result1 = AccountRegistry.delete_by_pesel(deleting_pesel)
        result2=AccountRegistry.search_by_pesel(deleting_pesel)
        self.assertEqual(result1, result2, f"Pesel nie jest znaleziony: {name}")




import unittest
from unittest.mock import mock_open, patch
import json
class TestRegisterOfAccountsWithJSON(unittest.TestCase):

    def setUp(self):
        self.konto1 = PersonalAccount(imie="Jan", nazwisko="Kowalski", pesel="12345678901")
        self.konto1.saldo = 100
        self.konto1.historia = ["Wpłata 100 PLN"]

        self.konto2 = PersonalAccount(imie="Anna", nazwisko="Nowak", pesel="98765432101")
        self.konto2.saldo = 50
        self.konto2.historia = ["Wpłata 50 PLN"]

        AccountRegistry.add_account(self.konto1)
        AccountRegistry.add_account(self.konto2)

    def tearDown(self):
        AccountRegistry.registry.clear()  

    def test_saveToJson(self):
        mock_file = mock_open()

        with patch("builtins.open", mock_file):
            AccountRegistry.saveToJson('../backup.json')  

        mock_file.assert_called_once_with('../backup.json', 'w')
        handle = mock_file()

        expected_data = [
            {
                "imie": "Jan", "nazwisko": "Kowalski", "pesel": "12345678901", "saldo": 100, "historia": ["Wpłata 100 PLN"]
            },
            {
                "imie": "Anna", "nazwisko": "Nowak", "pesel": "98765432101", "saldo": 50, "historia": ["Wpłata 50 PLN"]
            }
        ]

        written_data = "".join([call[0][0] for call in handle.write.call_args_list])  

        expected_json = json.dumps(expected_data, ensure_ascii=False, indent=4)

        self.assertEqual(written_data, expected_json)

    def test_loadFromJson(self):
        mock_file = mock_open(read_data=json.dumps([
            {
                "imie": "Jan", "nazwisko": "Kowalski", "pesel": "12345678901", "saldo": 100, "historia": ["Wpłata 100 PLN"]
            },
            {
                "imie": "Anna", "nazwisko": "Nowak", "pesel": "98765432101", "saldo": 50, "historia": ["Wpłata 50 PLN"]
            }
        ]))

        with patch("builtins.open", mock_file):
            AccountRegistry.loadFromJson('../backup.json')  

        self.assertEqual(len(AccountRegistry.registry), 2)

        self.assertEqual(AccountRegistry.registry[0].imie, "Jan")
        self.assertEqual(AccountRegistry.registry[0].nazwisko, "Kowalski")
        self.assertEqual(AccountRegistry.registry[0].pesel, "12345678901")
        self.assertEqual(AccountRegistry.registry[0].saldo, 100)
        self.assertEqual(AccountRegistry.registry[0].historia, ["Wpłata 100 PLN"])

        self.assertEqual(AccountRegistry.registry[1].imie, "Anna")
        self.assertEqual(AccountRegistry.registry[1].nazwisko, "Nowak")
        self.assertEqual(AccountRegistry.registry[1].pesel, "98765432101")
        self.assertEqual(AccountRegistry.registry[1].saldo, 50)
        self.assertEqual(AccountRegistry.registry[1].historia, ["Wpłata 50 PLN"])

        mock_file.assert_called_once_with('../backup.json', 'r')
