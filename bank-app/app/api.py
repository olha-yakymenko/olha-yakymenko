from flask import Flask, request, jsonify
from app.AccountRegistry import AccountRegistry
from app.PersonalAccount import PersonalAccount
app = Flask(__name__)
@app.route("/api/accounts", methods=['POST'])
def create_account():
    data = request.get_json()
    if not data or "imie" not in data or "nazwisko" not in data or "pesel" not in data:
        return jsonify({"error": "Missing required fields"}), 400
    used=AccountRegistry.search_by_pesel(data["pesel"])
    if used == None:
        print(f"Create account request: {data}")
        account = PersonalAccount(data["imie"], data["nazwisko"], data["pesel"])
        AccountRegistry.add_account(account)
        print(f"Account created: {account}") 
        return jsonify({"message": "Account created"}), 201
    else:
        return jsonify({"message": "This pesel was used"}), 409

@app.route("/api/accounts/<pesel>/transfer", methods=['POST'])
def transfers(pesel):
    account = AccountRegistry.search_by_pesel(pesel)
    if account is None:
        return jsonify({"message": "Konto nie zostało znalezione"}), 404
    
    data = request.get_json()
    
    if "type" not in data or "amount" not in data:
        return jsonify({"message": "Brakuje typu lub kwoty"}), 400

    if data["amount"] <= 0:
        return jsonify({"message": "Kwota jest ujemna"}), 400
    
    if data["type"] == "outgoing":
        wynik = account.przelew_wychodzacy(data["amount"])  
        if not wynik:
            return jsonify({"message": "Za mała kwota"}), 409
    elif data["type"] == "incoming":
        account.przelew_przychodzacy(data["amount"])  
    elif data["type"] == "express":
        wynik = account.szybki_przelew(data["amount"])
        if not wynik:
            return jsonify({"message": "Za mała kwota"}), 409
    else:
        return jsonify({"message": "Nieznany typ przelewu"}), 400
    
    return jsonify({"message": "Zlecenie przyjęto do realizacji"}), 200



@app.route("/api/accounts/<pesel>", methods=['GET'])
def get_account_by_pesel(pesel):
    account=AccountRegistry.search_by_pesel(pesel)
    if account is None:
        return jsonify({"message": "konta brak"}), 404
    return jsonify({"imie": account.imie, "nazwisko": account.nazwisko, "saldo": account.saldo}), 200

@app.route("/api/accounts/count", methods=['GET'])
def how_many_accounts():
    result=AccountRegistry.get_accounts_count()
    return jsonify({"Ilosc kont": result}), 200


@app.route("/api/accounts/<pesel>", methods=['PATCH'])
def update_account(pesel):
    data = request.get_json()
    account = AccountRegistry.search_by_pesel(pesel)
    if account is None:
        return jsonify({"message": "konta brak"}), 404
    if "imie" in data:
        account.imie = data["imie"]
    if "nazwisko" in data:
        account.nazwisko = data["nazwisko"]
    if "pesel" in data:
        account.pesel = data["pesel"]
    if "saldo" in data:
        account.saldo = data["saldo"]


    return jsonify({"message": "Account updated"}), 200


@app.route("/api/accounts/<pesel>", methods=['DELETE'])
def delete_account(pesel):
    account = AccountRegistry.search_by_pesel(pesel)
    if account is None:
        return jsonify({"message": "konta brak"}), 404
    AccountRegistry.delete_by_pesel(pesel)
    return jsonify("Konto usuniete"), 201

@app.route('/api/backup/dump/json', methods=['POST'])
def dump_json_backup():
    try:
        AccountRegistry.saveToJson('backup.json')
        return jsonify({"message": "Backup successfully created in JSON format."}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/backup/load/json', methods=['POST'])
def load_json_backup():
    try:
        AccountRegistry.loadFromJson('backup.json')
        return jsonify({"message": "Backup successfully loaded from JSON."}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500