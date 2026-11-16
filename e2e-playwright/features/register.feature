Feature: Registration
  Visitors create an account with email, password and a security question.

  Background:
    Given I am on the registration page

  Scenario: New customer registers and logs in
    When I register with a new email
    Then I am taken to the login page
    And I can log in with the new account

  Scenario: Email already in use is refused
    Given I have an account
    When I register with my email
    Then I see the registration error "Email must be unique"
    And I am still on the registration page

  Scenario Outline: Registration is blocked
    Given I have answered the security question
    When I fill in the email "<email>", the password "<password>" and the repeated password "<repeated>"
    Then I see the field error "<error>"
    And the register button is disabled

    Examples:
      | problem                   | email          | password    | repeated    | error                                  |
      | the email is invalid      | notanemail     | Password123 | Password123 | Email address is not valid.            |
      | the password is too short | user@gmail.com | abc         | abc         | Password must be 5-40 characters long. |
      | the passwords differ      | user@gmail.com | Password123 | Other123    | Passwords do not match                 |
