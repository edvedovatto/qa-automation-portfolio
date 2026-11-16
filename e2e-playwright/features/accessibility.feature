Feature: Accessibility
  Pages are scanned with axe against WCAG 2.1 AA. Problems the app already has are listed in the README and skipped, so a scenario only fails when a new one shows up.

  Scenario: The shop has no new accessibility problems
    Given I am on the shop
    Then the page has no new accessibility problems

  Scenario: The login page has no new accessibility problems
    Given I am on the login page
    Then the page has no new accessibility problems

  Scenario: The registration page has no new accessibility problems
    Given I am on the registration page
    Then the page has no new accessibility problems

  Scenario: The basket with a product has no new accessibility problems
    Given I am a logged in customer
    And I am on the shop
    When I add "Apple Juice (1000ml)" to the basket
    And I open the basket
    Then the basket has 1 "Apple Juice (1000ml)"
    And the page has no new accessibility problems
