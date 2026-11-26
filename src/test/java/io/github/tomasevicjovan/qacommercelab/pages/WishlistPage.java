package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.pages.components.ProductGrid;
import io.github.tomasevicjovan.qacommercelab.support.Browser;

public final class WishlistPage {

    private final Browser browser;
    private final ProductGrid grid;

    public WishlistPage(Browser browser) {
        this.browser = browser;
        this.grid = new ProductGrid(browser, "wishlist-grid");
    }

    public ProductGrid grid() {
        return grid;
    }

    public void open() {
        browser.open("#wishlist");
    }

    public void shouldList(String productName) {
        browser.eventually(grid::names, names -> names.contains(productName), "the wishlist to list " + productName);
    }
}
