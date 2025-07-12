import os
from .Konto import Konto
import requests
from datetime import datetime


class CompanyAccount(Konto):
    express_fee=5
    email_text="Historia konta Twojej firmy to: "
    def __init__(self,nazwa,nip):
        super().__init__()
        self.nazwa=nazwa
        if len(nip)!=10:
            self.nip="Niepoprawny NIP!"
        elif self.is_nip_valid(nip):
            self.nip=nip
        else:
            raise ValueError("Company not registered!!")
        
    @classmethod
    def is_nip_valid(cls, nip):
        gov_url = os.getenv('BANK_APP_MF_URL', 'https://wl-test.mf.gov.pl/')
        today = datetime.today().strftime('%Y-%m-%d')
        nip_path = f"{gov_url}api/search/nip/{nip}/?date={today}"
        print(f"Wysylanie zapytania do {nip_path}")
        response = requests.get(nip_path)
        print(f"Response dla nipu: {response.status_code}, {response.json()}")
        if response.status_code == 200:
            return True
        return False

    def zaciagnij_kredyt(self, kwota):
        if self.saldo >= 2 * kwota and 1775 in self.historia :
            self.saldo += kwota
        else:
            print(f"Warunki kredytu nie zostały spełnione. Saldo: {self.saldo}, Kwota kredytu: {kwota}")

