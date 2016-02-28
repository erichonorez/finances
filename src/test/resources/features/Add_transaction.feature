Feature: Add a transaction

  In order to record the actual state of a an account
  As a user
  I want to add a transaction to a account
  
  Scenario:
    Given an account with a initial balance of 20 euro
    When I add a expense of 5 euro
    Then the balance should be 15 euro
    And the expense should be in the list of the transaction
