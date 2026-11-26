@wishlist
Feature: Wishlist
  Signed-in customers save products for later and move them to the bag.

  Scenario: A product saved with the heart appears on the wishlist
    Given a signed-in customer
    When they save "Brew Pour-Over Kettle" to the wishlist from the product list
    Then the wishlist page lists "Brew Pour-Over Kettle"
    And the wishlist count shows 1

  Scenario: A product moves from the wishlist to the bag
    Given a signed-in customer with "Fold Travel Organizer" on the wishlist
    When they add "Fold Travel Organizer" to the bag from the wishlist page
    Then the bag count shows 1

  Scenario: A visitor has to sign in to save a product
    Given a visitor on the home page
    When they try to save "Brew Pour-Over Kettle" to the wishlist from the product list
    Then they see the notification "Sign in to save products to your wishlist."
