Feature: Checkout
  Customers pay for the basket choosing address, delivery and card.

  Background:
    Given I am a logged in customer
    And I have a saved address and card
    And I am on the shop
    And I add "Apple Juice (1000ml)" to the basket
    And I start the checkout

  Scenario: Placing an order confirms it and empties the basket
    When I choose my address, standard delivery and my card
    And I place the order
    Then I see the order confirmation
    And the basket counter goes back to 0

  Scenario: Cannot continue without choosing an address
    Then I see my saved address
    And I cannot continue without choosing an address

  Scenario: Cannot continue without choosing a card
    When I choose my address and standard delivery
    Then I see my saved card
    And I cannot continue without choosing a card
