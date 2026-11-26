package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;

import static io.github.tomasevicjovan.qacommercelab.support.Text.xpathLiteral;

/** Subtotal, discount, shipping and total, as shown in the bag, at checkout and on the order page. */
public final class Totals {

    private final Browser browser;
    private final String scope;

    public Totals(Browser browser, String scopeXPath) {
        this.browser = browser;
        this.scope = scopeXPath;
    }

    public String value(String row) {
        return browser.text(By.xpath("%s//div[contains(@class,'totals')]/div[span[1][normalize-space()=%s]]/span[last()]".formatted(scope, xpathLiteral(row))));
    }

    public void shouldShow(String row, String expected) {
        browser.eventually(() -> value(row), expected::equals, "%s to be %s".formatted(row, expected));
    }
}
