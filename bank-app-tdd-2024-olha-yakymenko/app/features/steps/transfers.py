from behave import *
import requests
from unittest_assertions import AssertEqual

assert_equal = AssertEqual()
URL = "http://localhost:5000" 

def create_account_with_balance(pesel, balance):
    json_body = {
        "imie": "Jan",
        "nazwisko": "Kowalski",
        "pesel": pesel
    }
    response = requests.post(URL + "/api/accounts", json=json_body)
    assert_equal(response.status_code, 201)

    json_body = {"saldo": balance}
    response = requests.patch(URL + f"/api/accounts/{pesel}", json=json_body)
    assert_equal(response.status_code, 200)

    return pesel


@Given('Account "{pesel}" has a balance of "{balance}"')
def step_impl_account_has_balance(context, pesel, balance):
    json_body = {"saldo": int(balance)}
    response = requests.patch(URL + f"/api/accounts/{pesel}", json=json_body)
    assert response.status_code == 200  

@When('I transfer money from account with pesel "{pesel}" with type "{transfer_type}" and amount "{amount}"')
def step_impl_transfer_money(context, pesel, transfer_type, amount):
    data = {
        "type": transfer_type, 
        "amount": int(amount)
    }
    response = requests.post(URL + f"/api/accounts/{pesel}/transfer", json=data)
    context.response = response

@Then('Account with pesel "{pesel}" has a balance of "{balance}"')
def step_impl_account_balance(context, pesel, balance):
    response = requests.get(URL + f"/api/accounts/{pesel}")
    assert response.status_code == 200  
    json_response = response.json()
    assert_equal(str(json_response["saldo"]), balance) 

@Then('The response message is "{message}"')
def step_impl_response_message(context, message):
    assert_equal(context.response.json()["message"], message)



@given('All accounts are cleared from the registry')
def step_impl_clear_all_accounts(context):
    response = requests.delete("http://localhost:5000/api/accounts/clear")

    print(f"Response Status Code: {response.status_code}")
    print(f"Response Text: {response.text}")  

    if response.status_code == 200:
        assert response.text == "All accounts have been cleared"
    else:
        assert response.status_code == 200, f"Nie udało się wyczyścić kont: {response.text}"

@When('I transfer money to account with pesel "{pesel}" with type "{transfer_type}" and amount "{amount}"')
def step_impl_incoming_transfer(context, pesel, transfer_type, amount):
    data = {
        "type": transfer_type,
        "amount": int(amount)
    }
    response = requests.post(URL + f"/api/accounts/{pesel}/transfer", json=data)
    context.response = response

@then(u'The response code is {status_code}')
def step_impl_check_response_code(context, status_code):
    if not hasattr(context, 'response'):
        raise AssertionError("Brak odpowiedzi w context.response")
    
    try:
        expected_status_code = int(status_code)
    except ValueError:
        raise AssertionError(f"Niepoprawny format status_code: {status_code}, powinno być liczba całkowita")
    
    actual_status_code = context.response.status_code
    
    assert actual_status_code == expected_status_code, \
        f"Expected status code {expected_status_code}, got {actual_status_code}. Odpowiedź: {context.response.text}"

@when(u'I transfer money from account with pesel "{pesel}" with type "{transfer_type}"')
def step_impl_transfer_money(context, pesel, transfer_type):
    data = {
        "type": transfer_type,
        "amount": 200  
    }

    response = requests.post(f"http://localhost:5000/api/accounts/{pesel}/transfer", json=data)

    context.response = response


@given('Account with pesel "{pesel}" does not exist in registry')
def check_account_with_pesel_does_not_exist(context, pesel):
    response = requests.get(URL + f"/api/accounts/{pesel}")
    assert_equal(response.status_code, 404)

@when('I send a transfer request from account with pesel "{pesel}" without amount and type')
def step_impl_send_transfer_request(context, pesel):
    url = f"http://localhost:5000/api/accounts/{pesel}/transfer"
    response = requests.post(url, json={})
    context.response = response  
