@account
Feature: Account
  Customers create an account or sign in before they can keep a bag, a wishlist or orders.
  Rules: CF-01 in qa-commerce-lab docs/BUSINESS.md.

  @smoke
  Scenario: A new customer creates an account
    Given a visitor on the home page
    When they create an account with a new email address
    Then they are signed in under their name
    And they see the notification "Account created"

  Scenario Outline: Registration explains what is wrong with the form
    Given a visitor on the home page
    When they try to create an account with <problem>
    Then the <field> field shows "<message>"
    And they are still signed out

    Examples:
      | problem                     | field    | message                                                   |
      | a password of 7 characters  | password | Use at least 8 characters                                 |
      | passwords that do not match | confirm  | The passwords do not match                                |
      | an email without a domain   | email    | Enter a valid email address, for example name@example.com |
      | an email that is registered | email    | This email is already registered                          |

  @smoke
  Scenario: A returning customer signs in
    Given a visitor on the home page
    When they sign in with the demo account
    Then they are signed in under the demo account name

  Scenario: Signing in with a wrong password is refused
    Given a visitor on the home page
    When they sign in to the demo account with a wrong password
    Then the sign-in form shows "Invalid email or password"
    And they are still signed out

  Scenario: A signed-in customer signs out
    Given a signed-in customer on the home page
    When they sign out
    Then they are signed out
    And they see the notification "You have been signed out."
