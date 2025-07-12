import unittest
from unittest.mock import patch, MagicMock
from app.BlackList import BlackList

class TestBlackList(unittest.TestCase):
    
    @patch('pymongo.MongoClient')  
    def setUp(self, mock_mongo_client):
        self.mock_client = MagicMock()
        mock_mongo_client.return_value = self.mock_client
        self.black_list_connection = BlackList()

    def tearDown(self):
        self.black_list_connection.close()

    @patch('pymongo.collection.Collection.insert_one')  
    @patch.object(BlackList, 'is_account_on_black_list', return_value=False)  
    def test_add_account_to_black_list(self, mock_is_account_on_black_list, mock_insert_one):
        mock_insert_one.return_value = MagicMock(inserted_id='mock_id')

        self.black_list_connection.add_account_to_black_list('12345678901', 'fraud')
        mock_insert_one.assert_called_once_with({
            'pesel': '12345678901',
            'reason': 'fraud'
        })
        mock_is_account_on_black_list.assert_called_once_with('12345678901')

    @patch('pymongo.collection.Collection.find_one')
    def test_is_account_blocked(self, mock_find_one):
        mock_find_one.return_value= {'pesel': '12345678901', 'reason': 'fraud'}
        result = self.black_list_connection.is_account_on_black_list('12345678901')
        self.assertTrue(result)
        mock_find_one.assert_called_once_with({'pesel': '12345678901'})
        mock_find_one.return_value=None 
        result=self.black_list_connection.is_account_on_black_list('34532456789')
        self.assertFalse(result)
        mock_find_one.assert_called_with({'pesel': '34532456789'})
        self.black_list_connection.close()
        