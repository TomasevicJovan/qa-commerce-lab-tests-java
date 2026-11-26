@checkout
Feature: Checkout and payment
  Customers pay with a new card, a saved card or the test wallet. The shop checks every field before
  it accepts an order. Rules: CF-03, CF-14 and CF-15 in qa-commerce-lab docs/BUSINESS.md.

  Background:
    Given a signed-in customer with 1 × "Pure Bamboo Toothbrush Set" in the bag

  @smoke
  Scenario: Paying with a new card and saving it
    When they check out with a new Visa card and keep "Save this card" ticked
    Then the order is confirmed and paid with "Visa •••• 4242"
    And the Visa card is saved for next time

  Scenario: Paying with a saved card
    Given they have a saved Mastercard
    When they check out with the saved Mastercard
    Then the order is confirmed and paid with "Mastercard •••• 4444"

  Scenario: Paying with the test wallet
    When they check out with the test wallet
    Then the order is confirmed and paid with "Test wallet"

  Scenario: A declined card cancels the order without a charge
    When they check out with the declined test card
    Then the order page shows "Payment failed"
    And the payment for the order has failed

  Scenario Outline: The card form explains what is wrong
    When they try to check out with a new card where <problem>
    Then the <field> field shows "<message>"
    And no order is placed

    Examples:
      | problem                      | field  | message                                             |
      | the number fails the check   | number | Enter a valid card number                           |
      | the card expired last month  | expiry | This card has expired                               |
      | the cardholder name is empty | holder | Enter the cardholder name as it appears on the card |

  Scenario: An expired coupon has to be removed before paying
    When they enter the coupon "EXPIRED" at checkout
    Then the coupon message says "✗ This code has expired. Remove it to place your order."

  Scenario: The delivery address is required
    When they try to check out with an empty delivery address
    Then they see the notification "Order not placed"
    And the delivery address fields show:
      | field      | message                  |
      | name       | Enter the recipient name |
      | line1      | Enter the street address |
      | city       | Enter the city           |
      | postalCode | Enter the postal code    |
    And no order is placed
