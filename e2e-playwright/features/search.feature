Feature: Product search
  Anyone can search the catalog by name.

  Background:
    Given I am on the shop

  @smoke
  Scenario: Search shows only the matching products
    When I search for "apple"
    Then I see the product "Apple Juice (1000ml)"
    And I do not see the product "Banana Juice (1000ml)"

  Scenario: Search with no match says nothing was found
    When I search for "zzzz"
    Then I see that no results were found
