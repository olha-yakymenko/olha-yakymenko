import json
from .PersonalAccount import PersonalAccount

class AccountRegistry:
    registry = []


    @classmethod
    def add_account(cls, account):
            cls.registry.append(account)

    @classmethod 
    def get_accounts_count(cls):
        return len(cls.registry)
    @classmethod
    def search_by_pesel(cls, pesel):
        for i in cls.registry:
                if i.pesel==pesel:
                    return i
        return None
    @classmethod
    def delete_by_pesel(cls, pesel):
        for konto in cls.registry:
            if konto.pesel == pesel:
                cls.registry.remove(konto)
        return None


    @classmethod
    def saveToJson(cls, file_path='backup.json'):
        with open(file_path, 'w') as file:
            json.dump([{
                "imie": konto.imie,
                "nazwisko": konto.nazwisko,
                "pesel": konto.pesel,
                "saldo": konto.saldo,
                "historia": konto.historia
            } for konto in cls.registry], file, ensure_ascii=False, indent=4)
        return None

    @classmethod
    def loadFromJson(cls, file_path='backup.json'):
        with open(file_path, 'r') as file:
            konto_dicts = json.load(file)
            cls.registry = []  
            for konto_dict in konto_dicts:
                konto = PersonalAccount(konto_dict["imie"], konto_dict["nazwisko"], konto_dict["pesel"])
                konto.saldo = konto_dict["saldo"]
                konto.historia = konto_dict["historia"]
                cls.registry.append(konto)
        return None
