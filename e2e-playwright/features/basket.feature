Feature: Basket
  Customers keep the products they want to buy in a basket.

  Rule: A logged in customer can manage the basket

    Background:
      Given I am a logged in customer
      And I am on the shop

    @smoke
    Scenario: Adding a product puts it in the basket
      When I add "Apple Juice (1000ml)" to the basket
      And I open the basket
      Then the basket has 1 "Apple Juice (1000ml)"

    Scenario: Adding the same product twice increases the quantity
      When I add "Apple Juice (1000ml)" to the basket
      And I add "Apple Juice (1000ml)" to the basket
      And I open the basket
      Then the basket has 2 "Apple Juice (1000ml)"

    Scenario: Removing the only product empties the basket
      Given I add "Apple Juice (1000ml)" to the basket
      When I open the basket
      And I remove "Apple Juice (1000ml)"
      Then the basket is empty

  Rule: A visitor cannot check out

    Scenario: Visitor has an empty basket and cannot check out
      When I open the basket
      Then the basket is empty
      And the checkout button is disabled
