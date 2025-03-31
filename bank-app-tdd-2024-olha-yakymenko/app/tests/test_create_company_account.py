import unittest
from unittest.mock import patch
from ..CompanyAccount import CompanyAccount
from unittest.mock import patch, MagicMock 
from ..SMTPClient import SMTPClient
from datetime import datetime

class TestTworzenieKontaFirmowego(unittest.TestCase):
    nazwa = "Januszex sp. z o.o"
    nip = "8461627563"  

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_tworzenie_konta(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = True
        pierwsze_konto = CompanyAccount(self.nazwa, self.nip)
        
        self.assertEqual(pierwsze_konto.nazwa, self.nazwa, "Nazwa firmy nie została zapisane!")
        self.assertEqual(pierwsze_konto.saldo, 0, "Saldo nie jest zerowe!")
        self.assertEqual(pierwsze_konto.nip, self.nip, "NIP nie zostało zapisane!")

    def test_zbyt_dlugi_nip(self):
        konto = CompanyAccount(self.nazwa, "84616275639887")
        self.assertEqual(konto.nip, "Niepoprawny NIP!", "NIP jest za długi, powinno być 'Niepoprawny NIP!'")

    def test_zbyt_krotki_nip(self):
        konto = CompanyAccount(self.nazwa, "846162")
        self.assertEqual(konto.nip, "Niepoprawny NIP!", "NIP jest za krótki, powinno być 'Niepoprawny NIP!'")
    
    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_incorrect_account(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = False
        incorrect_nip = "asdfghjklp"  
        with self.assertRaises(ValueError):
            konto = CompanyAccount(self.nazwa, incorrect_nip)

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_valid_nip_but_invalid_response(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = False
        
        with patch('requests.get') as mock_get:
            mock_get.return_value.status_code = 404 
            incorrect_nip = "8461627563"  
            with self.assertRaises(ValueError):
                konto = CompanyAccount(self.nazwa, incorrect_nip)
            
    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_correct_nip_with_valid_api_response(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = True
        
        with patch('requests.get') as mock_get:
            mock_get.return_value.status_code = 200  
            valid_nip = "8461627563"
            konto = CompanyAccount(self.nazwa, valid_nip)
            
            self.assertEqual(konto.nip, valid_nip)
            self.assertEqual(konto.nazwa, self.nazwa)

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_invalid_nip_with_404(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = False
    
        with patch('requests.get') as mock_get:
            mock_get.return_value.status_code = 404
            incorrect_nip = "8461627563"  
            with self.assertRaises(ValueError):
                konto = CompanyAccount(self.nazwa, incorrect_nip)

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_invalid_nip_with_500(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = False
    
        with patch('requests.get') as mock_get:
            mock_get.return_value.status_code = 500
            incorrect_nip = "8461627563"  
            with self.assertRaises(ValueError):
                konto = CompanyAccount(self.nazwa, incorrect_nip)

    def test_empty_nip(self):
        konto = CompanyAccount(self.nazwa, "")
        self.assertEqual(konto.nip, "Niepoprawny NIP!", "NIP nie może być pusty!")

    @patch('app.CompanyAccount.CompanyAccount.is_nip_valid')
    def test_valid_nip_with_unexpected_api_status(self, mock_is_nip_valid):
        mock_is_nip_valid.return_value = True
        
        with patch('requests.get') as mock_get:
            mock_get.return_value.status_code = 202 
            valid_nip = "8461627563"
            konto = CompanyAccount(self.nazwa, valid_nip)
            
            self.assertEqual(konto.nip, valid_nip)
            self.assertEqual(konto.nazwa, self.nazwa)

    @patch('requests.get')
    def test_api_response_400(self, mock_get):
        mock_get.return_value.status_code = 400
        mock_get.return_value.json.return_value = {"error": "Bad Request"}
        
        with self.assertRaises(ValueError):
            konto = CompanyAccount(self.nazwa, self.nip)
            
    @patch('requests.get')
    def test_api_response_403(self, mock_get):
        mock_get.return_value.status_code = 403
        mock_get.return_value.json.return_value = {"error": "Forbidden"}
        
        with self.assertRaises(ValueError):
            konto = CompanyAccount(self.nazwa, self.nip)

    @patch('requests.get')
    def test_express_fee(self, mock_get):
        mock_get.return_value.status_code = 200  
        mock_get.return_value.json.return_value = {"valid": True}  
        konto = CompanyAccount(self.nazwa, self.nip)
        self.assertEqual(konto.express_fee, 5)  

