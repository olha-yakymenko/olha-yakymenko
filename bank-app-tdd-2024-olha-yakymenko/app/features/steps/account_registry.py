from behave import *
import requests
from unittest_assertions import AssertEqual

assert_equal = AssertEqual()
URL = "http://localhost:5000"


@when('I create an account using name: "{name}", last name: "{last_name}", pesel: "{pesel}"')
def create_account(context, name, last_name, pesel):
    
    json_body = {
        "imie": f"{name}",
        "nazwisko": f"{last_name}",
        "pesel": pesel
    }
    create_resp = requests.post(URL + "/api/accounts", json=json_body)
    assert_equal(create_resp.status_code, 201)

@step('Number of accounts in registry equals: "{count}"')
def is_account_count_equal_to(context, count):
    response = requests.get(URL + "/api/accounts/count")
    assert_equal(response.status_code, 200)
    json_response = response.json()

    assert_equal(str(json_response["Ilosc kont"]), count)

@step('Account with pesel "{pesel}" exists in registry')
def check_account_with_pesel_exists(context, pesel):
    response = requests.get(URL + f"/api/accounts/{pesel}")
    assert_equal(response.status_code, 200)

@step('Account with pesel "{pesel}" does not exist in registry')
def check_account_with_pesel_does_not_exist(context, pesel):
    response = requests.get(URL + f"/api/accounts/{pesel}")
    assert_equal(response.status_code, 404)

@when('I delete account with pesel: "{pesel}"')
def delete_account(context, pesel):
    response = requests.delete(URL + f"/api/accounts/{pesel}")
    assert_equal(response.status_code, 201)

@when('I update "{field}" of account with pesel: "{pesel}" to "{value}"')
def update_field(context, field, pesel, value):
    field_mapping = {
        "name": "imie",
        "surname": "nazwisko",
        "pesel": "pesel",
        "balance": "saldo"
    }
    
    if field not in field_mapping:
        valid_fields = ", ".join(field_mapping.keys())
        raise ValueError(f"Invalid field: {field}. Must be one of {valid_fields}.")

    mapped_field = field_mapping[field]
    json_body = {mapped_field: value}
    response = requests.patch(URL + f"/api/accounts/{pesel}", json=json_body)
    
    if response.status_code != 200:
        print(f"Error updating {field} for {pesel}: {response.status_code} - {response.text}")
    
    assert response.status_code == 200, f"Error updating {field} for {pesel}: {response.text}"

@then('Account with pesel "{pesel}" has "{field}" equal to "{value}"')
def field_equals_to(context, pesel, field, value):
    field_mapping = {
        "name": "imie",
        "surname": "nazwisko",
        "pesel": "pesel",
        "balance": "saldo"
    }
    
    if field not in field_mapping:
        valid_fields = ", ".join(field_mapping.keys())
        raise ValueError(f"Invalid field: {field}. Must be one of {valid_fields}.")

    mapped_field = field_mapping[field]
    
    response = requests.get(URL + f"/api/accounts/{pesel}")
    assert response.status_code == 200, f"Account with pesel {pesel} not found: {response.text}"
    
    json_response = response.json()
    
    assert mapped_field in json_response, f"Field {mapped_field} not found in response: {json_response}"
    
    assert json_response[mapped_field] == value, f"Expected {mapped_field} to be {value}, but got {json_response[mapped_field]}"
