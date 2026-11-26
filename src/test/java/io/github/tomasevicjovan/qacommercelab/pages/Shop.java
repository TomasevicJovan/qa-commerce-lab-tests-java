package io.github.tomasevicjovan.qacommercelab.pages;

import io.github.tomasevicjovan.qacommercelab.pages.components.AuthDialog;
import io.github.tomasevicjovan.qacommercelab.pages.components.BagDialog;
import io.github.tomasevicjovan.qacommercelab.pages.components.Header;
import io.github.tomasevicjovan.qacommercelab.pages.components.Notifications;
import io.github.tomasevicjovan.qacommercelab.support.Browser;

/** Parts of the page that every route shares. */
public final class Shop {

    private final Browser browser;
    private final Header header;
    private final AuthDialog authDialog;
    private final Notifications notifications;
    private final BagDialog bag;

    public Shop(Browser browser) {
        this.browser = browser;
        this.header = new Header(browser);
        this.authDialog = new AuthDialog(browser);
        this.notifications = new Notifications(browser);
        this.bag = new BagDialog(browser);
    }

    public void openHome() {
        browser.open("#home");
    }

    public Header header() {
        return header;
    }

    public AuthDialog authDialog() {
        return authDialog;
    }

    public Notifications notifications() {
        return notifications;
    }

    public BagDialog bag() {
        return bag;
    }
}
