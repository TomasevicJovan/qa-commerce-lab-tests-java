package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.pages.components.CardFacts;
import io.github.tomasevicjovan.qacommercelab.pages.components.ProductGrid;
import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public final class ProductListPage {

    private static final By PRODUCTS = By.id("products");
    private static final By CARDS = By.cssSelector("#products article.product");
    private static final By EMPTY_STATE = By.cssSelector("#products .empty");
    private static final By NEXT_PAGE = By.id("next");
    private static final By PAGE_INDICATOR = By.id("page-indicator");

    private final Browser browser;
    private final ProductGrid grid;

    public ProductListPage(Browser browser) {
        this.browser = browser;
        this.grid = new ProductGrid(browser, "products");
    }

    public ProductGrid grid() {
        return grid;
    }

    public void open(String query) {
        browser.open(query.isEmpty() ? "#shop" : "#shop?" + query);
        waitForProducts();
    }

    public void waitForProducts() {
        browser.eventually(
                () -> !"true".equals(browser.waitFor(PRODUCTS).getDomAttribute("aria-busy")) && (browser.isShown(CARDS) || browser.isShown(EMPTY_STATE)),
                Boolean::booleanValue,
                "the product list to load");
    }

    public void sortBy(String label) {
        browser.select(By.id("sort"), label);
        browser.eventually(browser::currentHash, hash -> hash.contains("sort="), "the sort to be applied");
        waitForProducts();
    }

    public void emptyStateShouldRead(String text) {
        browser.shouldHaveText(EMPTY_STATE, text);
    }

    /** Card facts from every page of the list, following the Next button. */
    public List<CardFacts> allCards() {
        waitForProducts();
        var cards = new ArrayList<>(grid.facts());
        while (browser.read(this::hasNextPage)) {
            var indicator = browser.text(PAGE_INDICATOR);
            browser.click(NEXT_PAGE);
            browser.eventually(() -> browser.text(PAGE_INDICATOR), text -> !text.equals(indicator), "the next page");
            waitForProducts();
            cards.addAll(grid.facts());
        }
        return cards;
    }

    private boolean hasNextPage() {
        return browser.all(NEXT_PAGE).stream().findFirst()
                .filter(next -> !"true".equals(next.getDomAttribute("aria-disabled")) && next.isEnabled())
                .isPresent();
    }
}
