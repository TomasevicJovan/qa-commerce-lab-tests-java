package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static io.github.tomasevicjovan.qacommercelab.support.Text.xpathLiteral;

/** Header: search, account, wishlist and bag, deal links and popular subcategories. */
public final class Header {

    private static final By SEARCH = By.id("search");
    private static final By ACCOUNT = By.id("account");
    private static final By CREATE_ACCOUNT = By.id("register-open");
    private static final By SIGN_OUT = By.id("sign-out");
    private static final By BAG = By.id("cart-open");
    private static final By BAG_COUNT = By.id("cart-count");
    private static final By WISHLIST_COUNT = By.id("wishlist-count");

    private final Browser browser;

    public Header(Browser browser) {
        this.browser = browser;
    }

    public void search(String text) {
        browser.type(SEARCH, text);
        browser.waitFor(SEARCH).sendKeys(Keys.ENTER);
    }

    public void openSignIn() {
        browser.click(ACCOUNT);
    }

    public void openRegistration() {
        browser.click(CREATE_ACCOUNT);
    }

    public void signOut() {
        browser.click(ACCOUNT);
        browser.click(SIGN_OUT);
    }

    public void openBag() {
        browser.click(BAG);
    }

    public void openBlackFriday() {
        browser.click(By.cssSelector("[data-nav-link='black-friday']"));
    }

    public void openFreeShipping() {
        browser.click(By.cssSelector("[data-nav-link='free-shipping']"));
    }

    public void openPopularSubcategory(String name) {
        browser.click(By.xpath("//*[@id='popular-categories']//a[normalize-space()=%s]".formatted(xpathLiteral(name))));
    }

    /** "Sign in" when signed out, "Name ▾" when signed in. */
    public void accountShouldRead(String text) {
        browser.shouldHaveText(ACCOUNT, text);
    }

    public void bagCountShouldBe(int count) {
        browser.shouldHaveText(BAG_COUNT, String.valueOf(count));
    }

    public void wishlistCountShouldBe(int count) {
        browser.shouldHaveText(WISHLIST_COUNT, String.valueOf(count));
    }
}
