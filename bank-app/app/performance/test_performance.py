import unittest
import requests

class PerformanceTest(unittest.TestCase):
    body = {
        "imie" : "Dariusz",
        "nazwisko": "Januszewski",
        "pesel": "12345678901"
    }
    url = "http://localhost:5000/api/accounts"
    iteration_count = 100
    timeout = 0.5

    def test_create_delete_perf_test(self):
        for i in range(self.iteration_count):
            create_response = requests.post(self.url, json=self.body, timeout=self.timeout)
            self.assertEqual(create_response.status_code, 201)
            delete_response = requests.delete(f"{self.url}/{self.body['pesel']}", timeout=self.timeout)
            self.assertEqual(delete_response.status_code, 201)

    def test_transfer_perf(self):
        create_response = requests.post(self.url, json=self.body, timeout=1)
        self.assertEqual(create_response.status_code, 201)
        for i in range(self.iteration_count):
            transfer_response = requests.post(f"{self.url}/{self.body['pesel']}/transfer",
                                              json={"type": "incoming", "amount": 100}, timeout=self.timeout)
            self.assertEqual(transfer_response.status_code, 200)
        account = requests.get(f"{self.url}/{self.body['pesel']}", timeout=self.timeout)
        print(account.json())
        self.assertEqual(account.json()["saldo"], 100 * self.iteration_count)
        delete_response = requests.delete(f"{self.url}/{self.body['pesel']}", timeout=self.timeout)
        self.assertEqual(delete_response.status_code, 201)
