package io.github.tomasevicjovan.qacommercelab.support;

import io.github.tomasevicjovan.qacommercelab.api.clients.ApiSession;
import io.github.tomasevicjovan.qacommercelab.api.clients.AuthClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.CardsClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.CartClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.InventoryClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.OrdersClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.WishlistClient;
import io.github.tomasevicjovan.qacommercelab.api.model.CartItem;
import io.github.tomasevicjovan.qacommercelab.api.model.CheckoutRequest;
import io.github.tomasevicjovan.qacommercelab.api.model.Credentials;
import io.github.tomasevicjovan.qacommercelab.api.model.NewCard;
import io.github.tomasevicjovan.qacommercelab.api.model.Order;
import io.github.tomasevicjovan.qacommercelab.api.model.SavedCard;
import io.github.tomasevicjovan.qacommercelab.data.Cards;
import io.github.tomasevicjovan.qacommercelab.data.Pricing;
import io.github.tomasevicjovan.qacommercelab.data.Pricing.Line;
import io.github.tomasevicjovan.qacommercelab.data.ProductInfo;
import io.github.tomasevicjovan.qacommercelab.data.TestCard;

import java.util.List;

/**
 * Prepares customers and their state through the API, then hands the browser over signed in.
 * Every scenario gets its own new customer, so scenarios do not depend on each other and can run in parallel.
 */
public final class Customers {

    private final AuthClient auth;
    private final CartClient cart;
    private final WishlistClient wishlist;
    private final CardsClient cards;
    private final OrdersClient orders;
    private final InventoryClient inventory;
    private final Browser browser;
    private final RandomData random;
    private final ScenarioState state;

    public Customers(AuthClient auth, CartClient cart, WishlistClient wishlist, CardsClient cards, OrdersClient orders,
                     InventoryClient inventory, Browser browser, RandomData random, ScenarioState state) {
        this.auth = auth;
        this.cart = cart;
        this.wishlist = wishlist;
        this.cards = cards;
        this.orders = orders;
        this.inventory = inventory;
        this.browser = browser;
        this.random = random;
        this.state = state;
    }

    /** A different, unrelated customer with API clients of their own. */
    public record OtherCustomer(CartClient cart, OrdersClient orders) {
    }

    public Credentials newCredentials() {
        return random.credentials();
    }

    /** Registers a new customer through the API and signs the browser in as that customer. */
    public void signedIn() {
        state.credentials(newCredentials());
        var session = auth.register(state.credentials());
        browser.signInWithToken(session.token());
    }

    /** Replaces the bag through the API and reloads, so the storefront shows it. Remembers the stock before. */
    public void withBag(List<Line> lines) {
        for (var line : lines) {
            ensureStock(line.product(), line.quantity());
            state.stockBefore().put(line.product().id(), inventory.stock(line.product().id()));
        }
        cart.set(items(lines));
        browser.reload();
    }

    public void withWishlist(ProductInfo product) {
        wishlist.add(product.id());
        browser.reload();
    }

    public SavedCard withSavedCard(TestCard card) {
        return cards.save(new NewCard(random.cardholder(), card.digits(), Cards.validExpiry(), card.cvc()));
    }

    /** Places an order with the test wallet through the API, waits until it settles and remembers it. */
    public Order paidWithWallet(List<Line> lines) {
        lines.forEach(line -> ensureStock(line.product(), line.quantity()));
        cart.set(items(lines));
        var order = placeAndSettle(orders, lines, null);
        state.orderIds().add(order.id());
        return order;
    }

    /** Places the current bag through the given client: with the wallet, or with a new card when one is given. */
    public Order placeAndSettle(OrdersClient client, List<Line> lines, NewCard card) {
        var total = Pricing.calculate(lines, null, card == null ? null : brandOf(card)).total();
        var checkout = card == null
                ? CheckoutRequest.withWallet(random.address(), total)
                : CheckoutRequest.withNewCard(random.address(), card, total);
        return client.settled(client.place(checkout).id());
    }

    public OtherCustomer another() {
        var session = new ApiSession();
        new AuthClient(session).register(newCredentials());
        return new OtherCustomer(new CartClient(session), new OrdersClient(session));
    }

    public NewCard newCard(TestCard card) {
        return new NewCard(random.cardholder(), card.digits(), Cards.validExpiry(), card.cvc());
    }

    /**
     * The lab cannot restock: once a product is sold out on a local lab, only a reset brings it back.
     * This stops the scenario with that advice instead of failing later for an unrelated reason.
     */
    public void ensureStock(ProductInfo product, int quantity) {
        var stock = inventory.stock(product.id());
        if (stock < quantity) {
            throw new IllegalStateException(("The lab has %d unit(s) of %s, this scenario needs %d. "
                    + "Earlier runs bought the stock; reset the lab data (see README) and run again.").formatted(stock, product.name(), quantity));
        }
    }

    public static List<CartItem> items(List<Line> lines) {
        return lines.stream().map(l -> new CartItem(l.product().id(), l.quantity())).toList();
    }

    private static String brandOf(NewCard card) {
        return card.number().startsWith("5") ? "mastercard" : card.number().startsWith("4") ? "visa" : null;
    }
}
