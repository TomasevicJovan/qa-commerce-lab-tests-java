package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;

import java.util.List;

public final class OrdersPage {

    private static final By ORDERS = By.cssSelector("#main article[data-order-id]");

    private final Browser browser;

    public OrdersPage(Browser browser) {
        this.browser = browser;
    }

    public void open() {
        browser.open("#orders");
    }

    public List<String> orderIds() {
        return browser.eventually(() -> browser.all(ORDERS).stream().map(o -> o.getDomAttribute("data-order-id")).toList(),
                ids -> !ids.isEmpty(), "orders to be listed");
    }

    public void statusShouldBe(String orderId, String status) {
        browser.shouldHaveText(By.cssSelector("#main [data-order-id='%s'] [data-testid='order-status']".formatted(orderId)), status);
    }
}
