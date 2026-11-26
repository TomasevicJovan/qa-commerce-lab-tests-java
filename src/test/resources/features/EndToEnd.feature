@e2e
Feature: End-to-end purchases
  Complete shopping journeys, from finding a product to the confirmed order in My orders.

  @smoke
  Scenario: A new customer buys a product with a new card and saves the card
    Given a visitor on the home page
    When they create an account with a new email address
    And they search for "Trail Insulated Bottle"
    And they add "Trail Insulated Bottle" to the bag from the product list
    And they continue from the bag to checkout
    And they check out with a new Visa card and keep "Save this card" ticked
    Then the order is confirmed and paid with "Visa •••• 4242"
    And My orders shows the order as confirmed
    And the Visa card is offered as a saved card at the next checkout

  Scenario: A returning customer combines Black Friday, SAVE10 and a Mastercard
    Given a signed-in customer with saved Visa and Mastercard cards
    When they open the Black Friday deals
    And they add "Brew Pour-Over Kettle" to the bag from the product list
    And they add "Pulse Wireless Controller" to the bag from the product list
    And they go to checkout and pay with the saved Mastercard using coupon "SAVE10"
    Then the order summary shows subtotal $128.00, discount $24.32, shipping $7.00 and total $110.68
    When they place the order
    Then the order is confirmed for $110.68

  Scenario: A declined card, then a successful payment with another card
    Given a signed-in customer with 1 × "Fold Travel Organizer" in the bag
    When they check out with the declined test card
    Then the order page shows "Payment failed"
    When they put the products back in the bag
    And they check out with a new Visa card
    Then the order is confirmed and paid with "Visa •••• 4242"
