Feature: Transfer money between accounts

# Scenario 1: User is able to create a new account
Scenario: User is able to create a new account
  Given Number of accounts in registry equals: "0"
  When I create an account using name: "kurt", last name: "cobain", pesel: "89092909876"
  Then Number of accounts in registry equals: "1"
  And Account with pesel "89092909876" exists in registry

# Scenario 2: Transfer money from an account
 Scenario: Successful outgoing transfer
    Given Account with pesel "89092909876" exists in registry
    And Account "89092909876" has a balance of "1000"
    When I transfer money from account with pesel "89092909876" with type "outgoing" and amount "200"
    Then Account with pesel "89092909876" has a balance of "800"
    And The response message is "Zlecenie przyjęto do realizacji"

# Scenario 3: Incoming transfer
Scenario: Successful incoming transfer
  Given Account with pesel "89092909876" exists in registry
  And Account "89092909876" has a balance of "1000"
  When I transfer money to account with pesel "89092909876" with type "incoming" and amount "300"
  Then Account with pesel "89092909876" has a balance of "1300"
  And The response message is "Zlecenie przyjęto do realizacji"

# Scenario 4: Express transfer
Scenario: Successful express transfer
  Given Account with pesel "89092909876" exists in registry
  And Account "89092909876" has a balance of "1000"
  When I transfer money from account with pesel "89092909876" with type "express" and amount "500"
  Then Account with pesel "89092909876" has a balance of "499"
  And The response message is "Zlecenie przyjęto do realizacji"

# Scenario 5: Transfer with insufficient funds
Scenario: Outgoing transfer with insufficient funds
  Given Account with pesel "89092909876" exists in registry
  And Account "89092909876" has a balance of "100"
  When I transfer money from account with pesel "89092909876" with type "outgoing" and amount "200"
  Then The response message is "Za mała kwota"
  And The response code is 409

# Scenario 6: Invalid transfer type
Scenario: Transfer with an invalid type
  Given Account with pesel "89092909876" exists in registry
  When I transfer money from account with pesel "89092909876" with type "invalid_type" and amount "100"
  Then The response message is "Nieznany typ przelewu"
  And The response code is 400

# Scenario 7: Missing amount or type
Scenario: Missing amount or type in transfer request
  Given Account with pesel "89092909876" exists in registry
  When I send a transfer request from account with pesel "89092909876" without amount and type
  Then The response message is "Brakuje typu lub kwoty"
  And The response code is 400


# Scenario 8: Negative amount
Scenario: Transfer with negative amount
  Given Account with pesel "89092909876" exists in registry
  When I transfer money from account with pesel "89092909876" with type "outgoing" and amount "-100"
  Then The response message is "Kwota jest ujemna"
  And The response code is 400

Scenario: User is able to delete last account
  Given Account with pesel "89092909876" exists in registry
  When I delete account with pesel: "89092909876"
  Then Account with pesel "89092909876" does not exist in registry
  And Number of accounts in registry equals: "0"
