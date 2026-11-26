package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.api.clients.CartClient;
import io.github.tomasevicjovan.qacommercelab.api.model.CartItem;
import io.github.tomasevicjovan.qacommercelab.data.Catalog;
import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.pages.ProductListPage;
import io.github.tomasevicjovan.qacommercelab.pages.Shop;
import io.github.tomasevicjovan.qacommercelab.pages.WishlistPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public final class BagSteps {

    private final Shop shop;
    private final ProductListPage list;
    private final WishlistPage wishlist;
    private final CartClient cart;

    public BagSteps(Shop shop, ProductListPage list, WishlistPage wishlist, CartClient cart) {
        this.shop = shop;
        this.list = list;
        this.wishlist = wishlist;
        this.cart = cart;
    }

    /** Uses the list on screen when it shows the product; otherwise searches for it, the way a customer would. */
    @When("they add {string} to the bag from the product list")
    public void theyAddFromTheList(String product) {
        if (!list.grid().names().contains(product)) {
            list.open("q=" + encode(product));
        }
        list.grid().addToBag(product);
    }

    @When("they open the bag")
    public void theyOpenTheBag() {
        shop.header().openBag();
    }

    @When("they increase {string} by one in the bag")
    public void theyIncrease(String product) {
        shop.header().openBag();
        shop.bag().increase(product);
    }

    @When("they decrease {string} by one in the bag")
    public void theyDecrease(String product) {
        shop.header().openBag();
        shop.bag().decrease(product);
    }

    @When("they continue from the bag to checkout")
    public void theyContinueToCheckout() {
        shop.header().openBag();
        shop.bag().continueToCheckout();
    }

    @Then("the bag count shows {int}")
    public void theBagCountShows(int count) {
        shop.header().bagCountShouldBe(count);
    }

    @Then("the bag lists {int} × {string}")
    public void theBagLists(int quantity, String product) {
        if (!shop.bag().isOpen()) {
            shop.header().openBag();
        }
        shop.bag().quantityShouldBe(product, quantity);
    }

    @Then("the bag subtotal is {word}")
    public void theBagSubtotalIs(String amount) {
        shop.bag().totals().shouldShow(UiText.SUBTOTAL, amount);
    }

    @Then("the bag is empty")
    public void theBagIsEmpty() {
        shop.bag().shouldBeEmpty();
        shop.header().bagCountShouldBe(0);
    }

    @Then("the saved bag still has {int} × {string}")
    public void theSavedBagStillHas(int quantity, String product) {
        assertThat(cart.get()).containsExactly(new CartItem(Catalog.byName(product).id(), quantity));
    }

    @When("they save {string} to the wishlist from the product list")
    public void theySaveToTheWishlist(String product) {
        list.open("q=" + encode(product));
        list.grid().toggleWishlist(product);
    }

    @When("they try to save {string} to the wishlist from the product list")
    public void theyTryToSaveToTheWishlist(String product) {
        theySaveToTheWishlist(product);
    }

    @When("they add {string} to the bag from the wishlist page")
    public void theyAddFromTheWishlist(String product) {
        wishlist.open();
        wishlist.grid().addToBag(product);
    }

    @Then("the wishlist page lists {string}")
    public void theWishlistPageLists(String product) {
        list.grid().wishlistHeartShouldBeFilled(product);
        wishlist.open();
        wishlist.shouldList(product);
    }

    @Then("the wishlist count shows {int}")
    public void theWishlistCountShows(int count) {
        shop.header().wishlistCountShouldBe(count);
    }

    private static String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
