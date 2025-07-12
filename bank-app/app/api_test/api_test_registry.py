import unittest
from unittest.mock import patch
import requests

class TestBackupAPI(unittest.TestCase):
    def setUp(self):
        self.base_url = "http://127.0.0.1:5000/api/backup"
    
    @patch('app.AccountRegistry.AccountRegistry.saveToJson')
    def test_dump_json_backup_success(self, mock_save):
        mock_save.return_value = None  
        
        response = requests.post(self.base_url + "/dump/json")
        self.assertEqual(response.status_code, 200, f"Nie udało się stworzyć backupu: {response.text}")
        self.assertIn("Backup successfully created", response.json()["message"], f"Niespodziewana wiadomość: {response.text}")

    @patch('app.AccountRegistry.AccountRegistry.loadFromJson')
    def test_load_json_backup_success(self, mock_load):
        mock_load.return_value = None  
        
        response = requests.post(self.base_url + "/load/json")
        self.assertEqual(response.status_code, 200, f"Nie udało się załadować backupu: {response.text}")
        self.assertIn("Backup successfully loaded", response.json()["message"], f"Niespodziewana wiadomość: {response.text}")
