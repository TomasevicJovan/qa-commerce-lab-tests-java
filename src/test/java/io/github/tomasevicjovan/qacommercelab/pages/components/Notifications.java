package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import io.github.tomasevicjovan.qacommercelab.support.Text;
import org.openqa.selenium.By;

/** Toast notifications in the corner of the page. */
public final class Notifications {

    private static final By ALL = By.cssSelector("#notifications .notification");

    private final Browser browser;

    public Notifications(Browser browser) {
        this.browser = browser;
    }

    /** Waits for a notification whose title or message contains the text. */
    public void shouldShow(String text) {
        browser.eventually(
                () -> browser.all(ALL).stream().map(n -> Text.clean(n.getText()).replace("\n", " ").replace("×", "").trim()).toList(),
                shown -> shown.stream().anyMatch(n -> n.contains(text)),
                "a notification containing \"%s\"".formatted(text));
    }
}
