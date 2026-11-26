package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;

import static io.github.tomasevicjovan.qacommercelab.support.Text.cssQuote;
import static io.github.tomasevicjovan.qacommercelab.support.Text.xpathLiteral;

/** The bag dialog. */
public final class BagDialog {

    private static final By EMPTY = By.cssSelector("dialog#dialog[open] .modal-body > .empty");
    private static final By CONTINUE_TO_CHECKOUT = By.id("checkout-open");

    private final Browser browser;
    private final Totals totals;

    public BagDialog(Browser browser) {
        this.browser = browser;
        this.totals = new Totals(browser, "//dialog[@id='dialog']");
    }

    public Totals totals() {
        return totals;
    }

    public boolean isOpen() {
        return browser.isShown(By.xpath("//dialog[@id='dialog' and @open]//h2[normalize-space()=%s]".formatted(xpathLiteral(UiText.BAG_HEADING))));
    }

    public void quantityShouldBe(String productName, int quantity) {
        browser.shouldHaveText(By.xpath(line(productName) + "//div[@class='qty']/span"), String.valueOf(quantity));
    }

    public void increase(String productName) {
        browser.click(By.cssSelector("dialog#dialog button[aria-label=%s]".formatted(cssQuote("Add one " + productName))));
    }

    public void decrease(String productName) {
        browser.click(By.cssSelector("dialog#dialog button[aria-label=%s]".formatted(cssQuote("Remove one " + productName))));
    }

    public void shouldBeEmpty() {
        browser.waitFor(EMPTY);
    }

    public void continueToCheckout() {
        browser.click(CONTINUE_TO_CHECKOUT);
    }

    private static String line(String productName) {
        return "//dialog[@id='dialog']//div[contains(@class,'cart-item')][.//h3[normalize-space()=%s]]".formatted(xpathLiteral(productName));
    }
}
