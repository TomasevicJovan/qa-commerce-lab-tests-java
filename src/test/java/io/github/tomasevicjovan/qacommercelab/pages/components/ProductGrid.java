package io.github.tomasevicjovan.qacommercelab.pages.components;

import io.github.tomasevicjovan.qacommercelab.support.Browser;
import io.github.tomasevicjovan.qacommercelab.support.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static io.github.tomasevicjovan.qacommercelab.support.Text.cssQuote;

/** A grid of product cards (product list, wishlist). */
public final class ProductGrid {

    private final Browser browser;
    private final String gridId;

    public ProductGrid(Browser browser, String gridId) {
        this.browser = browser;
        this.gridId = gridId;
    }

    public void addToBag(String productName) {
        browser.click(cardPart(productName, "button.add-button"));
    }

    public void toggleWishlist(String productName) {
        browser.click(cardPart(productName, "button.wish-button"));
    }

    public void wishlistHeartShouldBeFilled(String productName) {
        browser.eventually(() -> browser.waitFor(cardPart(productName, "button.wish-button")).getDomAttribute("aria-pressed"),
                "true"::equals, productName + " to be on the wishlist");
    }

    public List<CardFacts> facts() {
        return browser.read(() -> browser.all(By.cssSelector("#%s article.product".formatted(gridId))).stream().map(ProductGrid::factsOf).toList());
    }

    public List<String> names() {
        return facts().stream().map(CardFacts::name).toList();
    }

    private static CardFacts factsOf(WebElement card) {
        var price = card.findElement(By.cssSelector(".price"));
        var compare = card.findElements(By.cssSelector(".compare-price")).stream().map(e -> Text.parseMoney(e.getText())).filter(c -> c > 0).findFirst().orElse(null);
        var mark = card.findElements(By.cssSelector(".free-shipping")).stream().map(e -> e.getText().trim()).findFirst().orElse(null);
        return new CardFacts(
                card.findElement(By.cssSelector("h3")).getText().trim(),
                card.findElement(By.cssSelector("a.category")).getText().trim(),
                Text.parseMoney(price.getText()),
                String.valueOf(price.getDomAttribute("class")).contains("sale"),
                compare,
                mark);
    }

    private By cardPart(String productName, String part) {
        return By.cssSelector("#%s article.product[aria-label=%s] %s".formatted(gridId, cssQuote(productName), part));
    }
}
