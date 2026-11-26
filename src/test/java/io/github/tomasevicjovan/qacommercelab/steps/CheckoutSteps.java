package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.api.clients.CardsClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.CartClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.OrdersClient;
import io.github.tomasevicjovan.qacommercelab.api.model.ApiValues;
import io.github.tomasevicjovan.qacommercelab.api.model.CartItem;
import io.github.tomasevicjovan.qacommercelab.api.model.SavedCard;
import io.github.tomasevicjovan.qacommercelab.data.Cards;
import io.github.tomasevicjovan.qacommercelab.data.Catalog;
import io.github.tomasevicjovan.qacommercelab.data.TestCard;
import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.pages.CheckoutPage;
import io.github.tomasevicjovan.qacommercelab.pages.OrderPage;
import io.github.tomasevicjovan.qacommercelab.support.Browser;
import io.github.tomasevicjovan.qacommercelab.support.RandomData;
import io.github.tomasevicjovan.qacommercelab.support.ScenarioState;
import io.github.tomasevicjovan.qacommercelab.support.Text;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public final class CheckoutSteps {

    private final CheckoutPage checkout;
    private final OrderPage orderPage;
    private final OrdersClient orders;
    private final CardsClient cards;
    private final CartClient cart;
    private final Browser browser;
    private final RandomData random;
    private final ScenarioState state;

    public CheckoutSteps(CheckoutPage checkout, OrderPage orderPage, OrdersClient orders, CardsClient cards, CartClient cart,
                         Browser browser, RandomData random, ScenarioState state) {
        this.checkout = checkout;
        this.orderPage = orderPage;
        this.orders = orders;
        this.cards = cards;
        this.cart = cart;
        this.browser = browser;
        this.random = random;
        this.state = state;
    }

    @When("they check out with a new Visa card and keep \"Save this card\" ticked")
    public void theyCheckOutWithANewVisaAndSaveIt() {
        payWithNewCard(Cards.VISA, true);
    }

    @When("they check out with a new Visa card")
    public void theyCheckOutWithANewVisa() {
        payWithNewCard(Cards.VISA, true);
    }

    @When("they check out with the declined test card")
    public void theyCheckOutWithTheDeclinedCard() {
        payWithNewCard(Cards.DECLINED, false);
    }

    @When("they check out with the saved Mastercard")
    public void theyCheckOutWithTheSavedMastercard() {
        openWithAddress();
        checkout.chooseSavedCard(Cards.MASTERCARD.last4());
        placeOrder();
    }

    @When("they check out with the test wallet")
    public void theyCheckOutWithTheWallet() {
        openWithAddress();
        checkout.chooseWallet();
        placeOrder();
    }

    @When("^they go to checkout and pay with the saved (Visa|Mastercard) using coupon \"(.*)\"$")
    public void theyGoToCheckoutWithASavedCard(String card, String coupon) {
        openWithAddress();
        checkout.chooseSavedCard(Cards.byName(card).last4());
        if (!coupon.isEmpty()) {
            checkout.enterCoupon(coupon);
        }
    }

    @When("they place the order")
    public void theyPlaceTheOrder() {
        placeOrder();
    }

    @When("they try to check out with a new card where {}")
    public void theyTryANewCardWhere(String problem) {
        var card = Cards.VISA;
        var holder = random.cardholder();
        var number = card.digits();
        var expiry = Cards.validExpiry();
        switch (problem) {
            case "the number fails the check" -> number = number.substring(0, number.length() - 1) + (number.endsWith("0") ? "1" : "0");
            case "the card expired last month" -> expiry = Cards.expiryIn(-1);
            case "the cardholder name is empty" -> holder = "";
            default -> throw new IllegalArgumentException("Unknown card problem '%s'.".formatted(problem));
        }
        openWithAddress();
        checkout.chooseNewCard();
        checkout.fillNewCard(holder, number, expiry, card.cvc(), true);
        checkout.placeOrder();
    }

    @When("they enter the coupon {string} at checkout")
    public void theyEnterACoupon(String code) {
        openWithAddress();
        checkout.enterCoupon(code);
    }

    @When("they try to check out with an empty delivery address")
    public void theyTryWithAnEmptyAddress() {
        checkout.open();
        checkout.clearAddress();
        checkout.chooseWallet();
        checkout.placeOrder();
    }

    @Then("the coupon message says {string}")
    public void theCouponMessageSays(String message) {
        checkout.couponMessageShouldBe(message);
    }

    @Then("the delivery address fields show:")
    public void theAddressFieldsShow(DataTable errors) {
        errors.asMaps().forEach(row -> checkout.fieldErrorShouldBe(row.get("field"), row.get("message")));
    }

    @Then("^the order summary shows subtotal (\\S+), discount (\\S+), shipping (.+) and total (\\S+)$")
    public void theOrderSummaryShows(String subtotal, String discount, String shipping, String total) {
        checkout.totals().shouldShow(UiText.SUBTOTAL, subtotal);
        checkout.totals().shouldShow(UiText.DISCOUNT, "−" + discount);
        checkout.totals().shouldShow(UiText.SHIPPING, shipping);
        checkout.totals().shouldShow(UiText.TOTAL, total);
    }

    @Then("no order is placed")
    public void noOrderIsPlaced() {
        assertThat(browser.currentHash()).isEqualTo("#checkout");
        assertThat(orders.list()).isEmpty();
    }

    @Then("the order is confirmed and paid with {string}")
    public void theOrderIsConfirmedAndPaidWith(String payment) {
        orderPage.bannerShouldStartWith(UiText.PAYMENT_SUCCESSFUL);
        assertThat(orderPage.fact(UiText.PAYMENT_FACT)).isEqualTo(payment);
        assertThat(orders.get(state.lastOrderId()).status()).isEqualTo(ApiValues.CONFIRMED);
    }

    /** The banner shows the amount; the API confirms it is the amount the order was charged. */
    @Then("the order is confirmed for {word}")
    public void theOrderIsConfirmedFor(String total) {
        orderPage.bannerShouldStartWith(UiText.PAYMENT_SUCCESSFUL);
        var order = orders.get(state.lastOrderId());
        assertThat(order.status()).isEqualTo(ApiValues.CONFIRMED);
        assertThat(Text.money(order.total())).isEqualTo(total);
        assertThat(orders.payment(order.id()).amount()).isEqualTo(order.total());
    }

    @Then("the Visa card is saved for next time")
    public void theVisaCardIsSaved() {
        assertThat(cards.list()).extracting(SavedCard::brand, SavedCard::last4).contains(tuple(Cards.VISA.brand(), Cards.VISA.last4()));
    }

    @Then("the Visa card is offered as a saved card at the next checkout")
    public void theVisaIsOfferedNextTime() {
        cart.set(List.of(new CartItem(Catalog.TRAIL_BOTTLE.id(), 1)));
        checkout.open();
        checkout.savedCardShouldBeOffered(Cards.VISA.last4());
    }

    private void openWithAddress() {
        if (!checkout.isOpen()) {
            checkout.open();
        }
        checkout.waitForForm();
        checkout.fillAddress(random.address());
    }

    private void payWithNewCard(TestCard card, boolean save) {
        openWithAddress();
        checkout.chooseNewCard();
        checkout.fillNewCard(random.cardholder(), card.number(), Cards.validExpiry(), card.cvc(), save);
        placeOrder();
    }

    /** Places the order and remembers its id once the order page opens. */
    private void placeOrder() {
        checkout.placeOrder();
        state.orderIds().add(orderPage.idFromUrl());
    }
}
