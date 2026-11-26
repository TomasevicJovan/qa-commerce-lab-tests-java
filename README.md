# qa-commerce-lab-tests-java

[![Tests](https://github.com/TomasevicJovan/qa-commerce-lab-tests-java/actions/workflows/tests.yml/badge.svg)](https://github.com/TomasevicJovan/qa-commerce-lab-tests-java/actions/workflows/tests.yml)

BDD test automation in Java for **FORMA**, a practice online shop ([qa-commerce-lab](https://github.com/TomasevicJovan/qa-commerce-lab)). Built with Selenium WebDriver, Cucumber, JUnit Platform and REST Assured on Java 21.

The same shop is tested three ways:

| Repository | Stack | What it is for |
|---|---|---|
| [qa-commerce-lab-tests](https://github.com/TomasevicJovan/qa-commerce-lab-tests) | TypeScript, Playwright | The detailed suite: 456 tests, every field and message, OpenAPI contract checks |
| [qa-commerce-lab-tests-dotnet](https://github.com/TomasevicJovan/qa-commerce-lab-tests-dotnet) | C#, Selenium, Reqnroll | Business rules and shopping journeys in Gherkin |
| **qa-commerce-lab-tests-java** (this one) | Java, Selenium, Cucumber | The **same feature files** as the C# repository, implemented in Java |

The feature files here are identical to the ones in the C# repository. Gherkin describes how the shop should behave, not how the tests are written, so one specification can run on two stacks and both runs have to pass.

**Latest report:** [tomasevicjovan.github.io/qa-commerce-lab-tests-java](https://tomasevicjovan.github.io/qa-commerce-lab-tests-java/)

## What is covered

51 scenarios in 8 feature files, checked against the shop's [business rules](https://github.com/TomasevicJovan/qa-commerce-lab/blob/main/docs/BUSINESS.md):

| Feature | Scenarios | What it checks |
|---|---:|---|
| [Account](src/test/resources/features/Account.feature) | 8 | Registration, form errors, sign in, wrong password, sign out |
| [Finding products](src/test/resources/features/FindingProducts.feature) | 6 | Search, no results, subcategory, Black Friday, Free shipping, sorting |
| [Shopping bag](src/test/resources/features/Bag.feature) | 6 | Adding, quantities, the limit of 10, emptying, bag kept after sign-in, signed-out visitor |
| [Wishlist](src/test/resources/features/Wishlist.feature) | 3 | Saving with the heart, moving to the bag, signed-out visitor |
| [Prices, discounts and shipping](src/test/resources/features/Pricing.feature) | 10 | Every example of the pricing table: SAVE10, Mastercard 10%, Black Friday, the $150 limit, free-shipping bags, rounding |
| [Checkout and payment](src/test/resources/features/Checkout.feature) | 9 | New, saved and wallet payments, declined card, card form errors, expired coupon, required address |
| [Orders after checkout](src/test/resources/features/Orders.feature) | 6 | Order page, My orders, losing the last unit to another customer, restoring the bag, another customer's order, bank decline releasing stock |
| [End-to-end purchases](src/test/resources/features/EndToEnd.feature) | 3 | New customer from registration to My orders; Black Friday + SAVE10 + Mastercard; declined card, then a successful payment |

## How the tests are built

- **Gherkin describes behaviour, not clicks.** A step says what the customer does ("they check out with the saved Mastercard"). Selectors live only in page objects, so a changed screen means one change in one class.
- **Every scenario prepares its own state through the API.** Each scenario registers its own customer and sets up the bag, saved cards and earlier orders through REST Assured. The browser is used only for the behaviour under test. Scenarios do not depend on each other, and they run in parallel.
- **The API layer has a model and a client per resource.** Requests and responses are Java records (`CheckoutRequest`, `Order`, `SavedCard`...), and only the fields the scenarios use are modelled. Each resource has its own client (`AuthClient`, `CartClient`, `CardsClient`, `OrdersClient`, `InventoryClient`, `LabClient`) on one shared base. A client states which status codes it expects, and any other status fails with the method, path, status and body.
- **The UI result is confirmed behind the UI.** After a payment, the API confirms the order status and the amount actually charged, not only the banner on the page.
- **An order accepted is not an order paid.** The shop processes stock and payment in the background. Steps wait for the final state (confirmed, rejected or cancelled) instead of a fixed time. The UI layer has no fixed sleeps: every read retries until the expected state appears and, on a timeout, reports what the page actually showed.
- **Settings that affect everyone run alone.** The scenario that makes the bank decline every payment changes the whole shop, so it is tagged `@serial`. JUnit Platform runs it with the global lock, so no other scenario runs at the same time, and the lab is restored afterwards.
- **One object graph per scenario.** PicoContainer gives every scenario its own browser, API session, random data and state.
- **A failed scenario leaves a screenshot** in the HTML report.

## Test data

No test data is written inside the step definitions. Data comes from four places, each with its own rule:

| Kind | Where | Example |
|---|---|---|
| Secrets and environment | `local.properties` locally (ignored by git), repository secrets in CI; defaults in `config.properties` | Demo account email and password |
| Expected values (what the shop is supposed to do) | `data/` in the repository, and the examples in the feature files | Products and prices, test cards, coupons, shop texts |
| Values that only have to be valid | `support/RandomData.java` (Datafaker) | Customer names, emails, passwords, addresses, cardholders |
| State a scenario needs | `support/Customers.java`, through the API | A new customer, a bag, saved cards, a placed order |

Random data is seeded. The run seed is printed at the start of every run, and each scenario's seed (run seed + scenario name and line) is written to the report, so a scenario gets the same data on any thread. `TEST_SEED=<seed>` repeats a run with the same data. Invalid and boundary values (a 7-character password, a card that expired last month) are part of the test design and are written in the scenarios.

`data/Pricing.java` is an independent copy of the pricing rules, used to send the expected total when an order is prepared through the API.

## Project structure

```text
src/test/java/io/github/tomasevicjovan/qacommercelab/
  api/model/      Request and response records
  api/clients/    One REST Assured client per resource, on a shared base
  data/           Expected values: catalog, test cards, promotions, shop texts, the pricing rules
  pages/          Page objects (product list, wishlist, checkout, order, orders) and components (header, sign-in dialog, bag, totals, notifications)
  steps/          Thin step definitions: they call page objects, API clients and Customers, and hold no selectors
  support/        Browser and waiting, settings, random data, customers, hooks, scenario state
  runner/         The JUnit Platform suite
src/test/resources/
  features/                  Gherkin, the same files as in the C# repository
  config.properties          Non-secret defaults (lab address, browser size, timeouts)
  junit-platform.properties  Parallel execution, the @serial rule, the HTML report
```

## Run locally

You need Java 21, Google Chrome, and the [shop](https://github.com/TomasevicJovan/qa-commerce-lab) running on http://localhost:3000. Start the shop either with Docker (`docker compose up --build -d --wait`) or with `npm start`, not both. With Docker, `--build` matters: without it, Docker reuses images built from older code. Maven comes with the project (`mvnw`), and Selenium Manager downloads the matching ChromeDriver by itself.

Create `local.properties` in the project folder with the demo account. The values are in the shop's README, and the file is ignored by git:

```properties
demoEmail=<demo email>
demoPassword=<demo password>
```

Then:

```powershell
.\mvnw test                                            # everything (./mvnw on macOS and Linux)
.\mvnw test "-Dcucumber.filter.tags=@smoke"            # the critical path
.\mvnw test "-Dcucumber.filter.tags=@pricing"          # one feature, by its tag
.\mvnw test "-Dheadless=false"                         # in a visible browser window
```

The HTML report is written to `target/cucumber-report/index.html`.

**Reset the shop between full runs.** A full run buys products, and the shop cannot restock. Scenarios that need stock stop with a clear message when it has run out. Before running everything again, reset the shop's data:

```powershell
# in the qa-commerce-lab folder
docker compose down --volumes; docker compose up --build -d --wait   # with Docker
npm run reset                                                        # without Docker, with the shop stopped
```

CI always starts from fresh data.

## Continuous integration

[`.github/workflows/tests.yml`](.github/workflows/tests.yml) runs on every push to `main`, on pull requests, nightly and manually. It starts the shop with Docker (all services and Kafka) and runs the scenarios, taking the demo account from repository secrets. It uploads the HTML report and the JUnit results, and publishes the report to GitHub Pages from `main`. A branch tests the shop branch with the same name, if one exists.

## Tech stack

Java 21 · Maven · Selenium WebDriver 4 · Cucumber 8 · JUnit Platform 6 · PicoContainer · REST Assured 6 · Jackson · Datafaker · AssertJ · GitHub Actions · GitHub Pages
