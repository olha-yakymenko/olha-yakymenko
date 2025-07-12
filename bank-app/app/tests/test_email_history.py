import unittest
from unittest.mock import patch
from app.PersonalAccount import PersonalAccount
from app.CompanyAccount import CompanyAccount
from app.SMTPClient import SMTPClient
import datetime

class TestSendHistoryToEmail(unittest.TestCase):
    imie = "Dariusz"
    nazwisko = "Januszewski"
    pesel = "06211888888"
    nazwa = "Januszex sp. z o.o"
    nip = "8461627563"
    history = [334, 789]
    expected_email_text_personal = f"Twoja historia konta to: {history}"
    expected_email_text_company = f"Historia konta Twojej firmy to: {history}"
    date = datetime.datetime.today().strftime("%Y-%m-%d")
    expected_email_subject = f"Wyciąg z dnia {date}"
    email = "email@gmail.com"

    @patch("app.Konto.SMTPClient.send")
    def test_send_history_to_email_personal_account(self, send_mock):
        send_mock.return_value = True
        konto = PersonalAccount(self.imie, self.nazwisko, self.pesel)
        konto.historia = self.history
        result = konto.send_history_to_email(self.email)
        self.assertTrue(result)
        send_mock.assert_called_once()

        args, kwargs = send_mock.call_args
        self.assertEqual(args[0], self.expected_email_subject)
        self.assertEqual(args[1], self.expected_email_text_personal)
        self.assertEqual(args[2], self.email)

    @patch("app.Konto.SMTPClient.send")
    def test_send_history_to_email_company_account(self, send_mock):
        send_mock.return_value = True
        konto = CompanyAccount(self.nazwa, self.nip)
        konto.historia = self.history
        result = konto.send_history_to_email(self.email)
        self.assertTrue(result)
        send_mock.assert_called_once()

        args, kwargs = send_mock.call_args
        self.assertEqual(args[0], self.expected_email_subject)
        self.assertEqual(args[1], self.expected_email_text_company)
        self.assertEqual(args[2], self.email)

    def test_send_should_return_false(self):
        subject = "Test Subject"
        text = "Test Text"
        email_address = "test@email.com"
        result = SMTPClient.send(subject, text, email_address)
        self.assertFalse(result)

    @patch("app.Konto.SMTPClient.send")
    def test_send_history_to_email_should_return_false(self, send_mock):
        send_mock.return_value = False
        konto = PersonalAccount(self.imie, self.nazwisko, self.pesel)
        konto.historia = self.history
        result = konto.send_history_to_email(self.email)
        self.assertFalse(result)
        send_mock.assert_called_once()

        args, kwargs = send_mock.call_args
        self.assertEqual(args[0], self.expected_email_subject)
        self.assertEqual(args[1], self.expected_email_text_personal)
        self.assertEqual(args[2], self.email)

