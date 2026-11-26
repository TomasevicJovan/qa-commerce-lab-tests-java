package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.data.Cards;
import io.github.tomasevicjovan.qacommercelab.data.Catalog;
import io.github.tomasevicjovan.qacommercelab.data.Pricing.Line;
import io.github.tomasevicjovan.qacommercelab.pages.Shop;
import io.github.tomasevicjovan.qacommercelab.support.Customers;
import io.cucumber.java.en.Given;

import java.util.Arrays;
import java.util.List;

/** Who the scenario is about and what they already have: prepared through the API, not the UI. */
public final class CustomerSteps {

    private final Customers customers;
    private final Shop shop;

    public CustomerSteps(Customers customers, Shop shop) {
        this.customers = customers;
        this.shop = shop;
    }

    @Given("a visitor on the home page")
    public void aVisitor() {
        shop.openHome();
    }

    @Given("a signed-in customer")
    public void aSignedInCustomer() {
        customers.signedIn();
    }

    @Given("a signed-in customer on the home page")
    public void aSignedInCustomerOnTheHomePage() {
        customers.signedIn();
    }

    @Given("a signed-in customer with {int} × {string} in the bag")
    public void aCustomerWithABag(int quantity, String product) {
        customers.signedIn();
        customers.withBag(List.of(new Line(Catalog.byName(product), quantity)));
    }

    @Given("a signed-in customer with saved Visa and Mastercard cards")
    public void aCustomerWithSavedCards() {
        customers.signedIn();
        customers.withSavedCard(Cards.VISA);
        customers.withSavedCard(Cards.MASTERCARD);
    }

    @Given("a signed-in customer with {string} on the wishlist")
    public void aCustomerWithAWishlist(String product) {
        customers.signedIn();
        customers.withWishlist(Catalog.byName(product));
    }

    /** A bag written the way people write one: "2 × Shell Phone Case, 1 × Flow Yoga Mat". */
    @Given("^their bag holds (.+)$")
    public void theirBagHolds(String bag) {
        customers.withBag(parseBag(bag));
    }

    @Given("they have a saved Mastercard")
    public void aSavedMastercard() {
        customers.withSavedCard(Cards.MASTERCARD);
    }

    static List<Line> parseBag(String bag) {
        return Arrays.stream(bag.split(","))
                .map(line -> line.split("×", 2))
                .map(parts -> new Line(Catalog.byName(parts[1].trim()), Integer.parseInt(parts[0].trim())))
                .toList();
    }
}
