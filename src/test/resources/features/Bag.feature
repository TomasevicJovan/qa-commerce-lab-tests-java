@bag
Feature: Shopping bag
  Signed-in customers collect products in a bag that the shop saves for them.
  Rules: CF-02 in qa-commerce-lab docs/BUSINESS.md.

  @smoke
  Scenario: Adding a product puts it in the bag
    Given a signed-in customer
    When they add "Still Ceramic Mug" to the bag from the product list
    Then the bag count shows 1
    And the bag lists 1 × "Still Ceramic Mug"

  Scenario: Changing the quantity updates the bag total
    Given a signed-in customer with 1 × "Still Ceramic Mug" in the bag
    When they increase "Still Ceramic Mug" by one in the bag
    Then the bag lists 2 × "Still Ceramic Mug"
    And the bag subtotal is $48.00

  Scenario: A product cannot be added more than 10 times
    Given a signed-in customer with 10 × "Still Ceramic Mug" in the bag
    When they add "Still Ceramic Mug" to the bag from the product list
    Then they see the notification "Could not add to bag"
    And the saved bag still has 10 × "Still Ceramic Mug"

  Scenario: Removing the last unit empties the bag
    Given a signed-in customer with 1 × "Still Ceramic Mug" in the bag
    When they decrease "Still Ceramic Mug" by one in the bag
    Then the bag is empty

  Scenario: The bag is kept after signing out and in again
    Given a signed-in customer with 2 × "Still Ceramic Mug" in the bag
    When they sign out and sign in again
    Then the bag count shows 2

  Scenario: A visitor has to sign in to use the bag
    Given a visitor on the home page
    When they open the bag
    Then they see the notification "Sign in to see your bag."
    And the sign-in form is open
