package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.pages.ProductListPage;
import io.github.tomasevicjovan.qacommercelab.pages.Shop;
import io.github.tomasevicjovan.qacommercelab.pages.components.CardFacts;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public final class BrowsingSteps {

    private final Shop shop;
    private final ProductListPage list;

    public BrowsingSteps(Shop shop, ProductListPage list) {
        this.shop = shop;
        this.list = list;
    }

    @When("they search for {string}")
    public void theySearchFor(String text) {
        shop.header().search(text);
        list.waitForProducts();
    }

    @When("they open the {string} subcategory from the navigation")
    public void theyOpenASubcategory(String subcategory) {
        shop.header().openPopularSubcategory(subcategory);
        list.waitForProducts();
    }

    @When("they open the Black Friday deals")
    public void theyOpenBlackFriday() {
        shop.header().openBlackFriday();
        list.waitForProducts();
    }

    @When("they open the Free shipping products")
    public void theyOpenFreeShipping() {
        shop.header().openFreeShipping();
        list.waitForProducts();
    }

    @When("they open all products sorted by {string}")
    public void theyOpenAllProductsSortedBy(String sort) {
        list.open("");
        list.sortBy(sort);
    }

    @Then("the only result is {string}")
    public void theOnlyResultIs(String product) {
        assertThat(list.allCards()).extracting(CardFacts::name).containsExactly(product);
    }

    @Then("they see that no products were found")
    public void noProductsWereFound() {
        list.emptyStateShouldRead(UiText.NO_PRODUCTS_FOUND);
    }

    @Then("every product in the list belongs to {string}")
    public void everyProductBelongsTo(String subcategory) {
        assertThat(list.allCards()).isNotEmpty().allSatisfy(card -> assertThat(card.subcategory()).as(card.name()).isEqualTo(subcategory));
    }

    @Then("every product in the list shows a Black Friday price and its original price")
    public void everyProductIsOnADeal() {
        assertThat(list.allCards()).isNotEmpty().allSatisfy(card -> {
            assertThat(card.salePrice()).as("%s shows a sale price", card.name()).isTrue();
            assertThat(card.comparePrice()).as("%s shows its original price", card.name()).isNotNull().isGreaterThan(card.price());
        });
    }

    @Then("every product in the list is marked {string}")
    public void everyProductIsMarked(String mark) {
        assertThat(list.allCards()).isNotEmpty().allSatisfy(card -> assertThat(card.freeShippingMark()).as(card.name()).isEqualTo(mark));
    }

    @Then("the prices on the page go from low to high")
    public void thePricesGoUp() {
        assertThat(list.grid().facts()).isNotEmpty().extracting(CardFacts::price).isSortedAccordingTo(Comparator.naturalOrder());
    }
}
