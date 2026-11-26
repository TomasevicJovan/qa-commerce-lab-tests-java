@orders
Feature: Orders after checkout
  An accepted order is not a paid order: the shop processes payment and stock in the background and
  the order page follows it. Rules: order state machine, CF-04 and CF-05 in qa-commerce-lab docs/BUSINESS.md.

  Scenario: The order page shows what was bought, where it goes and how it was paid
    Given a signed-in customer who paid for 2 × "Still Ceramic Mug" with the test wallet
    When they open the order
    Then the order page shows "Payment successful"
    And it lists 2 × "Still Ceramic Mug"
    And it shows the delivery address and "Test wallet" as the payment

  Scenario: My orders lists the newest order first
    Given a signed-in customer who paid for 1 × "Still Ceramic Mug" with the test wallet
    And then paid for 1 × "Pure Bamboo Toothbrush Set" with the test wallet
    When they open My orders
    Then the orders are listed newest first

  Scenario: Losing the last unit to another customer means no charge
    Given a signed-in customer at checkout with the last "Pocket Mini Speaker"
    And another customer buys the last "Pocket Mini Speaker" first
    When they place the order with their saved Visa
    Then the order page shows "Order not completed"
    And the payment for the order was never taken

  Scenario: A failed order can put the products back in the bag
    Given a signed-in customer whose order for 1 × "Still Ceramic Mug" was declined
    When they open the order and put the products back in the bag
    Then they see the notification "Bag restored"
    And the bag count shows 1

  Scenario: A customer cannot open another customer's order
    Given another customer has placed an order
    And a signed-in customer
    When they open the other customer's order
    Then they see "Order not found"

  @serial
  Scenario: A payment declined by the bank releases the reserved stock
    Given the bank declines every payment
    And a signed-in customer with 1 × "Jump Speed Rope" in the bag
    When they check out with a new Visa card
    Then the order page shows "Payment failed"
    And the stock of "Jump Speed Rope" is the same as before the order
