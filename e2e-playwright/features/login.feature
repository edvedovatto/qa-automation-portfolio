Feature: Login
  Customers log in with email and password to reach their account.

  @smoke
  Scenario: Customer logs in with valid credentials
    Given I have an account
    And I am on the login page
    When I log in with my credentials
    Then I am taken to the shop
    And I am logged in

  Scenario: Wrong password is refused
    Given I have an account
    And I am on the login page
    When I log in with the password "wrongpassword"
    Then I see the login error "Invalid email or password."
    And I stay on the login page
    And I am not logged in

  Scenario: Unknown email gets the same error as a wrong password
    Given I am on the login page
    When I log in as "wrong@example.com" with the password "wrongpassword"
    Then I see the login error "Invalid email or password."
    And I stay on the login page

  Scenario: Login button is disabled while the form is empty
    Given I am on the login page
    Then the login button is disabled

  Scenario: Session survives a page reload
    Given I have an account
    And I have logged in with my credentials
    When I reload the page
    Then I am logged in

  Scenario: Logout ends the session
    Given I have an account
    And I have logged in with my credentials
    When I log out
    Then I am not logged in

  Scenario: Customer goes back to the page that asked for the login
    Given I have an account
    And I am on the login page coming from the basket
    When I log in with my credentials
    Then I am taken to the basket
