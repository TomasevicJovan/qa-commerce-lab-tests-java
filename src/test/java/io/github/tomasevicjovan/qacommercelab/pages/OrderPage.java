package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import io.github.tomasevicjovan.qacommercelab.support.Settings;
import io.github.tomasevicjovan.qacommercelab.support.Text;
import org.openqa.selenium.By;

import static io.github.tomasevicjovan.qacommercelab.support.Text.xpathLiteral;

public final class OrderPage {

    private static final By BANNER = By.cssSelector("#order-banner [data-status]");

    private final Browser browser;

    public OrderPage(Browser browser) {
        this.browser = browser;
    }

    public void open(String orderId) {
        browser.open("#order/" + orderId);
    }

    /** The order id from the address bar (#order/&lt;id&gt;), once the page is open. */
    public String idFromUrl() {
        return browser.eventually(browser::currentHash, hash -> hash.startsWith("#order/"), "the order page to open").substring("#order/".length());
    }

    /** Waits until the order has settled and the banner shows its final message. */
    public void bannerShouldStartWith(String title) {
        browser.eventually(() -> browser.all(BANNER).stream().findFirst().map(e -> Text.clean(e.getText())).orElse(null),
                text -> text != null && text.startsWith(title),
                "the order banner to start with \"%s\"".formatted(title),
                Settings.current().orderTimeout());
    }

    public String fact(String label) {
        return browser.text(By.xpath("//*[contains(@class,'order-facts')]//dt[normalize-space()=%s]/following-sibling::dd[1]".formatted(xpathLiteral(label))));
    }

    public String items() {
        return browser.text(By.cssSelector("#main .order-items"));
    }

    public void putProductsBackInBag() {
        browser.click(By.id("retry-order"));
    }

    public void notFoundMessageShouldBe(String text) {
        browser.shouldHaveText(By.cssSelector("#main .inline-error"), text);
    }
}
