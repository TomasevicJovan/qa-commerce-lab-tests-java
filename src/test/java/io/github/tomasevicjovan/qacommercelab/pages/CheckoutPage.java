package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.api.model.Address;
import io.github.tomasevicjovan.qacommercelab.pages.components.Totals;
import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.List;

import static io.github.tomasevicjovan.qacommercelab.support.Text.xpathLiteral;

public final class CheckoutPage {

    private static final By FORM = By.id("checkout-form");
    private static final By PLACE_ORDER = By.cssSelector("#checkout-form button.place-order");
    private static final By COUPON_STATUS = By.id("coupon-status");
    private static final List<String> ADDRESS_FIELDS = List.of("name", "line1", "city", "postalCode");

    private final Browser browser;
    private final Totals totals;

    public CheckoutPage(Browser browser) {
        this.browser = browser;
        this.totals = new Totals(browser, "//*[@id='checkout-total']");
    }

    public Totals totals() {
        return totals;
    }

    public void open() {
        browser.open("#checkout");
        waitForForm();
    }

    public void waitForForm() {
        browser.waitFor(PLACE_ORDER);
    }

    public boolean isOpen() {
        return browser.isShown(FORM);
    }

    public void fillAddress(Address address) {
        browser.type(input("name"), address.name());
        browser.type(input("line1"), address.line1());
        browser.type(input("city"), address.city());
        browser.type(input("postalCode"), address.postalCode());
    }

    public void clearAddress() {
        ADDRESS_FIELDS.forEach(field -> browser.type(input(field), ""));
    }

    public void chooseSavedCard(String last4) {
        browser.click(By.xpath(savedCardOption(last4) + "//input[@type='radio']"));
    }

    public void savedCardShouldBeOffered(String last4) {
        browser.waitFor(By.xpath(savedCardOption(last4)));
    }

    public void chooseNewCard() {
        browser.click(By.cssSelector("#checkout-form input[name='payment'][value='new']"));
    }

    public void chooseWallet() {
        browser.click(By.cssSelector("#checkout-form input[name='payment'][value='wallet']"));
    }

    /** Types a new card the way a customer does: digits only; the form adds the spaces and the slash. */
    public void fillNewCard(String holder, String number, String expiry, String cvc, boolean save) {
        browser.type(input("holder"), holder);
        browser.type(input("number"), number.replaceAll("\\D", ""));
        browser.type(input("expiry"), expiry.replaceAll("\\D", ""));
        browser.type(input("cvc"), cvc);
        var saveCard = browser.waitFor(input("saveCard"));
        if (saveCard.isSelected() != save) {
            saveCard.click();
        }
    }

    public void enterCoupon(String code) {
        browser.type(input("coupon"), code);
        browser.waitFor(input("coupon")).sendKeys(Keys.TAB);
    }

    public void couponMessageShouldBe(String message) {
        browser.shouldHaveText(COUPON_STATUS, message);
    }

    public void fieldErrorShouldBe(String field, String message) {
        browser.shouldHaveText(By.cssSelector("#checkout-form #error-" + field), message);
    }

    public void placeOrder() {
        browser.click(PLACE_ORDER);
    }

    private static String savedCardOption(String last4) {
        return "//*[@id='checkout-form']//div[contains(@class,'payment-option')][.//strong[normalize-space()=%s]]".formatted(xpathLiteral("•••• " + last4));
    }

    private static By input(String name) {
        return By.cssSelector("#checkout-form [name='%s']".formatted(name));
    }
}
