@finding-products
Feature: Finding products
  Visitors find products by search, by subcategory and through the Black Friday and Free shipping links.
  Rules: SF-02 to SF-05 and CF-02 in qa-commerce-lab docs/BUSINESS.md.

  @smoke
  Scenario: Search finds a product by its name
    Given a visitor on the home page
    When they search for "Arc Wireless Headphones"
    Then the only result is "Arc Wireless Headphones"

  Scenario: Search without a match says so
    Given a visitor on the home page
    When they search for "xyzzy-no-such-product"
    Then they see that no products were found

  Scenario: A popular subcategory link lists only that subcategory
    Given a visitor on the home page
    When they open the "Audio" subcategory from the navigation
    Then every product in the list belongs to "Audio"

  Scenario: Black Friday lists only products on a deal, with the original price
    Given a visitor on the home page
    When they open the Black Friday deals
    Then every product in the list shows a Black Friday price and its original price

  Scenario: Free shipping lists only products that ship free
    Given a visitor on the home page
    When they open the Free shipping products
    Then every product in the list is marked "✓ Free shipping"

  Scenario: Products can be sorted from the lowest price
    Given a visitor on the home page
    When they open all products sorted by "Price: low to high"
    Then the prices on the page go from low to high
