package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import org.openqa.selenium.By;

/** The sign-in and registration dialog. */
public final class AuthDialog {

    private static final By ROOT = By.cssSelector("dialog#dialog[open] #auth-form");
    private static final By FORM_ERROR = By.cssSelector("#auth-form .form-error");
    private static final By SUBMIT = By.cssSelector("#auth-form button[type='submit']");

    private final Browser browser;

    public AuthDialog(Browser browser) {
        this.browser = browser;
    }

    public void shouldBeOpen() {
        browser.waitFor(ROOT);
    }

    public void shouldBeClosed() {
        browser.shouldNotExist(ROOT);
    }

    public void register(String name, String email, String password, String confirm) {
        browser.type(field("name"), name);
        browser.type(field("email"), email);
        browser.type(field("password"), password);
        browser.type(field("confirm"), confirm);
        browser.click(SUBMIT);
    }

    public void signIn(String email, String password) {
        browser.type(field("email"), email);
        browser.type(field("password"), password);
        browser.click(SUBMIT);
    }

    public void formErrorShouldBe(String message) {
        browser.shouldHaveText(FORM_ERROR, message);
    }

    private static By field(String name) {
        return By.cssSelector("#auth-form [name='%s']".formatted(name));
    }
}
