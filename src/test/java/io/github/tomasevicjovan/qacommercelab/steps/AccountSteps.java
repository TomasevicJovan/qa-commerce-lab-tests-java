package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.api.clients.ApiSession;
import io.github.tomasevicjovan.qacommercelab.api.clients.AuthClient;
import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.pages.Shop;
import io.github.tomasevicjovan.qacommercelab.support.Browser;
import io.github.tomasevicjovan.qacommercelab.support.Customers;
import io.github.tomasevicjovan.qacommercelab.support.RandomData;
import io.github.tomasevicjovan.qacommercelab.support.ScenarioState;
import io.github.tomasevicjovan.qacommercelab.support.Settings;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;

public final class AccountSteps {

    private final Shop shop;
    private final Browser browser;
    private final Customers customers;
    private final AuthClient auth;
    private final RandomData random;
    private final ScenarioState state;
    private String demoName;

    public AccountSteps(Shop shop, Browser browser, Customers customers, AuthClient auth, RandomData random, ScenarioState state) {
        this.shop = shop;
        this.browser = browser;
        this.customers = customers;
        this.auth = auth;
        this.random = random;
        this.state = state;
    }

    /** Registers through the form; the API client then signs in too, so later steps can check the account's data. */
    @When("they create an account with a new email address")
    public void theyCreateAnAccount() {
        var credentials = customers.newCredentials();
        state.credentials(credentials);
        shop.header().openRegistration();
        shop.authDialog().register(credentials.name(), credentials.email(), credentials.password(), credentials.password());
        shop.authDialog().shouldBeClosed();
        auth.signIn(credentials.email(), credentials.password());
    }

    /** One invalid input at a time; the rest of the form is valid. */
    @When("they try to create an account with {}")
    public void theyTryToCreateAnAccountWith(String problem) {
        var valid = customers.newCredentials();
        var email = valid.email();
        var password = valid.password();
        var confirm = password;
        switch (problem) {
            case "a password of 7 characters" -> {
                password = random.password(7);
                confirm = password;
            }
            case "passwords that do not match" -> confirm = random.password();
            case "an email without a domain" -> email = valid.email().substring(0, valid.email().indexOf('@') + 1);
            case "an email that is registered" -> email = registeredEmail();
            default -> throw new IllegalArgumentException("Unknown registration problem '%s'.".formatted(problem));
        }
        shop.header().openRegistration();
        shop.authDialog().register(valid.name(), email, password, confirm);
    }

    @When("they sign in with the demo account")
    public void theySignInWithTheDemoAccount() {
        var settings = Settings.current();
        demoName = auth.signIn(settings.demoEmail(), settings.demoPassword()).user().name();
        shop.header().openSignIn();
        shop.authDialog().signIn(settings.demoEmail(), settings.demoPassword());
    }

    @When("they sign in to the demo account with a wrong password")
    public void theySignInWithAWrongPassword() {
        shop.header().openSignIn();
        shop.authDialog().signIn(Settings.current().demoEmail(), random.password());
    }

    @When("they sign out")
    public void theySignOut() {
        shop.header().signOut();
    }

    @When("they sign out and sign in again")
    public void theySignOutAndInAgain() {
        var credentials = state.credentials();
        shop.header().signOut();
        shop.header().accountShouldRead(UiText.SIGN_IN);
        shop.header().openSignIn();
        shop.authDialog().signIn(credentials.email(), credentials.password());
        shop.header().accountShouldRead(UiText.accountButton(credentials.name()));
    }

    @Then("they are signed in under their name")
    public void signedInUnderTheirName() {
        shop.header().accountShouldRead(UiText.accountButton(state.credentials().name()));
    }

    @Then("they are signed in under the demo account name")
    public void signedInAsDemo() {
        shop.header().accountShouldRead(UiText.accountButton(demoName));
    }

    @Then("they are signed out")
    public void signedOut() {
        shop.header().accountShouldRead(UiText.SIGN_IN);
    }

    @Then("they are still signed out")
    public void stillSignedOut() {
        shop.header().accountShouldRead(UiText.SIGN_IN);
    }

    @Then("the sign-in form shows {string}")
    public void theSignInFormShows(String message) {
        shop.authDialog().formErrorShouldBe(message);
    }

    @Then("the sign-in form is open")
    public void theSignInFormIsOpen() {
        shop.authDialog().shouldBeOpen();
    }

    /** Field errors share one pattern (#error-&lt;field&gt;) in the sign-in, registration and checkout forms. */
    @Then("the {word} field shows {string}")
    public void theFieldShows(String field, String message) {
        browser.shouldHaveText(By.id("error-" + field), message);
    }

    @Then("they see the notification {string}")
    public void theySeeTheNotification(String text) {
        shop.notifications().shouldShow(text);
    }

    private String registeredEmail() {
        var other = customers.newCredentials();
        new AuthClient(new ApiSession()).register(other);
        return other.email();
    }
}
