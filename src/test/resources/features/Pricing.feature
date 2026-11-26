@pricing
Feature: Prices, discounts and shipping
  Black Friday prices are the product prices. SAVE10 takes 10% off the subtotal, a Mastercard takes 10%
  off what is left, and shipping is free from $150 after discounts or when every product ships free.
  Each discount is rounded to the nearest cent on its own. The examples are the ones in
  qa-commerce-lab docs/BUSINESS.md ("Money, discounts and shipping").

  Scenario Outline: The order summary and the charged amount follow the pricing rules
    Given a signed-in customer with saved Visa and Mastercard cards
    And their bag holds <bag>
    When they go to checkout and pay with the saved <card> using coupon "<coupon>"
    Then the order summary shows subtotal <subtotal>, discount <discount>, shipping <shipping> and total <total>
    When they place the order
    Then the order is confirmed for <total>

    Examples:
      | bag                                            | coupon | card       | subtotal | discount | shipping | total   |
      | 1 × Arc Wireless Headphones                    |        | Visa       | $129.00  | $0.00    | $7.00    | $136.00 |
      | 1 × Arc Wireless Headphones                    | SAVE10 | Mastercard | $129.00  | $24.51   | $7.00    | $111.49 |
      | 2 × Orbit Desk Lamp                            |        | Visa       | $158.00  | $0.00    | On us    | $158.00 |
      | 2 × Orbit Desk Lamp                            | SAVE10 | Visa       | $158.00  | $15.80   | $7.00    | $149.20 |
      | 1 × Note Dotted Journal                        |        | Visa       | $18.00   | $0.00    | On us    | $18.00  |
      | 1 × Note Dotted Journal, 1 × Still Ceramic Mug |        | Visa       | $42.00   | $0.00    | $7.00    | $49.00  |
      | 2 × Shell Phone Case, 1 × Flow Yoga Mat        |        | Visa       | $77.00   | $0.00    | On us    | $77.00  |
      | 1 × Nova Gaming Chair                          |        | Visa       | $249.00  | $0.00    | On us    | $249.00 |
      | 1 × Pocket Notebook 3-pack                     | SAVE10 | Mastercard | $19.95   | $3.80    | $7.00    | $23.15  |
      | 1 × Twist Phone Grip                           | SAVE10 | Mastercard | $12.95   | $2.47    | $7.00    | $17.48  |
