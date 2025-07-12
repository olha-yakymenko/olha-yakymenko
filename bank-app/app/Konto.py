from datetime import datetime
from app.SMTPClient import SMTPClient

class Konto:
    def __init__(self):
        self.saldo=0
        self.historia=[]

    def przelew_wychodzacy(self, kwota):
        if self.saldo >= kwota:
            self.saldo -= kwota
            self.historia.append(-kwota)
            return True
        return False

    def przelew_przychodzacy(self, kwota):
        self.saldo += kwota
        self.historia.append(kwota)

    def szybki_przelew(self,kwota):
        if self.saldo>=kwota and kwota>0:
            self.saldo-=kwota+self.express_fee
            self.historia.append(-kwota)
            self.historia.append(-self.express_fee)
            return True
        else:
            print("Przelew nie zostal wykonany")
            return False
        
    def send_history_to_email(self, recipient_email):
        smtp_client=SMTPClient()
        today = datetime.now().strftime("%Y-%m-%d")
        subject = f"Wyciąg z dnia {today}"
        text = self.email_text+str(self.historia)
        return smtp_client.send(subject, text, recipient_email)