package io.github.tomasevicjovan.qacommercelab.support;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.picocontainer.Disposable;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * One Chrome window per scenario, plus the waiting helpers every page object uses.
 * The shop renders asynchronously, so nothing reads the page once: every read and every assertion
 * polls until the expected state appears or the wait runs out. There are no fixed sleeps in the UI layer.
 */
public final class Browser implements Disposable {

    private final WebDriver driver;

    public Browser() {
        var settings = Settings.current();
        var options = new ChromeOptions();
        if (settings.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=%d,%d".formatted(settings.windowWidth(), settings.windowHeight()),
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-search-engine-choice-screen");
        driver = new ChromeDriver(options);
    }

    /**
     * Opens a route of the single-page shop with a fresh page load. Going to another #hash on the same
     * document would only re-render with the data the page already holds, not what the API just prepared.
     */
    public void open(String hash) {
        var target = URI.create(Settings.current().baseUrl()).resolve("/" + hash).toString();
        var sameDocument = stripHash(driver.getCurrentUrl()).equals(stripHash(target));
        driver.navigate().to(target);
        if (sameDocument) {
            driver.navigate().refresh();
        }
        waitUntilReady();
    }

    /** Signs the browser in with an API token. The shop keeps its token in sessionStorage. */
    public void signInWithToken(String token) {
        driver.navigate().to(Settings.current().baseUrl());
        ((JavascriptExecutor) driver).executeScript("sessionStorage.setItem('qa-token', arguments[0]);", token);
        driver.navigate().refresh();
        waitUntilReady();
    }

    public void reload() {
        driver.navigate().refresh();
        waitUntilReady();
    }

    public String currentHash() {
        var fragment = URI.create(driver.getCurrentUrl()).getRawFragment();
        return fragment == null ? "" : "#" + fragment;
    }

    /** The app has finished booting (session, bag and wishlist loaded) when the promotion bar starts. */
    public void waitUntilReady() {
        waitFor(By.cssSelector("#promo-message .promo-count"), Duration.ofSeconds(20));
    }

    public WebElement waitFor(By by) {
        return waitFor(by, Settings.current().waitTimeout());
    }

    public WebElement waitFor(By by, Duration timeout) {
        return until(d -> d.findElements(by).stream().filter(WebElement::isDisplayed).findFirst().orElse(null), by + " to be visible", timeout);
    }

    public boolean isShown(By by) {
        return read(() -> driver.findElements(by).stream().anyMatch(WebElement::isDisplayed));
    }

    public List<WebElement> all(By by) {
        return driver.findElements(by);
    }

    public void click(By by) {
        until(d -> {
            waitFor(by).click();
            return true;
        }, by + " to be clickable", Settings.current().waitTimeout());
    }

    /** Replaces the value of an input; retries while a dialog or a re-render still covers it. */
    public void type(By by, String text) {
        until(d -> {
            var element = waitFor(by);
            element.clear();
            if (!text.isEmpty()) {
                element.sendKeys(text);
            }
            return true;
        }, by + " to accept typing", Settings.current().waitTimeout());
    }

    public void select(By by, String optionText) {
        new Select(waitFor(by)).selectByVisibleText(optionText);
    }

    public String text(By by) {
        return Text.clean(waitFor(by).getText());
    }

    public String textOrNull(By by) {
        return driver.findElements(by).stream().findFirst().map(e -> Text.clean(e.getText())).orElse(null);
    }

    public void shouldHaveText(By by, String expected) {
        eventually(() -> textOrNull(by), expected::equals, "text of %s to be \"%s\"".formatted(by, expected));
    }

    public void shouldNotExist(By by) {
        eventually(() -> driver.findElements(by).size(), count -> count == 0, by + " to disappear");
    }

    /**
     * Polls {@code read} until {@code isExpected} holds. Fails with the last value read, so a timeout says
     * what the page showed instead of only that it timed out.
     */
    public <T> T eventually(Supplier<T> read, Predicate<T> isExpected, String description) {
        return eventually(read, isExpected, description, Settings.current().waitTimeout());
    }

    public <T> T eventually(Supplier<T> read, Predicate<T> isExpected, String description, Duration timeout) {
        var last = new Object() { T value; };
        try {
            until(d -> {
                last.value = read.get();
                return isExpected.test(last.value);
            }, description, timeout);
        } catch (TimeoutException e) {
            throw new AssertionError("Expected %s, but it was: %s".formatted(description, describe(last.value)), e);
        }
        return last.value;
    }

    /** Reads the page once, starting again if the shop re-renders the part being read (a stale element). */
    public <T> T read(Supplier<T> read) {
        return until(d -> new Box<>(read.get()), "the page to stop re-rendering", Settings.current().waitTimeout()).value();
    }

    public byte[] screenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }


    @Override
    public void dispose() {
        driver.quit();
    }

    private <R> R until(Function<WebDriver, R> condition, String description, Duration timeout) {
        var wait = new WebDriverWait(driver, timeout, Duration.ofMillis(100));
        wait.withMessage("Waited for " + description);
        wait.ignoreAll(List.of(NoSuchElementException.class, StaleElementReferenceException.class,
                ElementNotInteractableException.class, ElementClickInterceptedException.class));
        return wait.until(d -> {
            var result = condition.apply(d);
            return Boolean.FALSE.equals(result) ? null : result;
        });
    }

    private static String stripHash(String url) {
        var index = url.indexOf('#');
        return index < 0 ? url : url.substring(0, index);
    }

    private static String describe(Object value) {
        return value instanceof String s ? "\"" + s + "\"" : Objects.toString(value, "nothing");
    }

    private record Box<T>(T value) {
    }
}
