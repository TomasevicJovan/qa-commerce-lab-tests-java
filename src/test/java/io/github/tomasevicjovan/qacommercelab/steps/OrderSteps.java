package io.github.tomasevicjovan.qacommercelab.steps;

import io.github.tomasevicjovan.qacommercelab.api.clients.CartClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.InventoryClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.LabClient;
import io.github.tomasevicjovan.qacommercelab.api.clients.OrdersClient;
import io.github.tomasevicjovan.qacommercelab.api.model.ApiValues;
import io.github.tomasevicjovan.qacommercelab.api.model.CartItem;
import io.github.tomasevicjovan.qacommercelab.api.model.LabConfig;
import io.github.tomasevicjovan.qacommercelab.data.Cards;
import io.github.tomasevicjovan.qacommercelab.data.Catalog;
import io.github.tomasevicjovan.qacommercelab.data.Pricing.Line;
import io.github.tomasevicjovan.qacommercelab.data.UiText;
import io.github.tomasevicjovan.qacommercelab.pages.CheckoutPage;
import io.github.tomasevicjovan.qacommercelab.pages.OrderPage;
import io.github.tomasevicjovan.qacommercelab.pages.OrdersPage;
import io.github.tomasevicjovan.qacommercelab.support.Customers;
import io.github.tomasevicjovan.qacommercelab.support.Poll;
import io.github.tomasevicjovan.qacommercelab.support.ScenarioState;
import io.github.tomasevicjovan.qacommercelab.support.Settings;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class OrderSteps {

    private final Customers customers;
    private final CheckoutPage checkout;
    private final OrderPage orderPage;
    private final OrdersPage ordersPage;
    private final OrdersClient orders;
    private final CartClient cart;
    private final InventoryClient inventory;
    private final LabClient lab;
    private final ScenarioState state;

    public OrderSteps(Customers customers, CheckoutPage checkout, OrderPage orderPage, OrdersPage ordersPage, OrdersClient orders,
                      CartClient cart, InventoryClient inventory, LabClient lab, ScenarioState state) {
        this.customers = customers;
        this.checkout = checkout;
        this.orderPage = orderPage;
        this.ordersPage = ordersPage;
        this.orders = orders;
        this.cart = cart;
        this.inventory = inventory;
        this.lab = lab;
        this.state = state;
    }

    @Given("a signed-in customer who paid for {int} × {string} with the test wallet")
    public void aCustomerWhoPaid(int quantity, String product) {
        customers.signedIn();
        customers.paidWithWallet(List.of(new Line(Catalog.byName(product), quantity)));
    }

    @Given("then paid for {int} × {string} with the test wallet")
    public void thenPaidFor(int quantity, String product) {
        customers.paidWithWallet(List.of(new Line(Catalog.byName(product), quantity)));
    }

    @Given("a signed-in customer at checkout with the last {string}")
    public void aCustomerAtCheckoutWithTheLast(String productName) {
        var product = Catalog.byName(productName);
        customers.signedIn();
        customers.withSavedCard(Cards.VISA);
        assertThat(inventory.stock(product.id()))
                .as("%s must have exactly one unit left. Earlier runs changed the stock; reset the lab data (see README).", product.name())
                .isEqualTo(1);
        customers.withBag(List.of(new Line(product, 1)));
        checkout.open();
    }

    @Given("another customer buys the last {string} first")
    public void anotherCustomerBuysTheLastOne(String productName) {
        var other = customers.another();
        var line = new Line(Catalog.byName(productName), 1);
        other.cart().set(Customers.items(List.of(line)));
        assertThat(customers.placeAndSettle(other.orders(), List.of(line), null).status()).isEqualTo(ApiValues.CONFIRMED);
    }

    @When("they place the order with their saved Visa")
    public void theyPlaceWithTheSavedVisa() {
        checkout.chooseSavedCard(Cards.VISA.last4());
        checkout.placeOrder();
        state.orderIds().add(orderPage.idFromUrl());
    }

    @Given("a signed-in customer whose order for {int} × {string} was declined")
    public void aDeclinedOrder(int quantity, String productName) {
        customers.signedIn();
        var line = new Line(Catalog.byName(productName), quantity);
        customers.ensureStock(line.product(), quantity);
        cart.set(Customers.items(List.of(line)));
        var order = customers.placeAndSettle(orders, List.of(line), customers.newCard(Cards.DECLINED));
        assertThat(order.status()).isEqualTo(ApiValues.CANCELLED);
        state.orderIds().add(order.id());
    }

    @Given("another customer has placed an order")
    public void anotherCustomersOrder() {
        var other = customers.another();
        var line = new Line(Catalog.PURE_TOOTHBRUSH, 1);
        customers.ensureStock(line.product(), 1);
        other.cart().set(List.of(new CartItem(line.product().id(), 1)));
        state.otherCustomersOrderId(customers.placeAndSettle(other.orders(), List.of(line), null).id());
    }

    @Given("the bank declines every payment")
    public void theBankDeclines() {
        lab.configure(new LabConfig(ApiValues.BANK_DECLINES));
    }

    @When("they open the order")
    public void theyOpenTheOrder() {
        orderPage.open(state.lastOrderId());
    }

    @When("they open My orders")
    public void theyOpenMyOrders() {
        ordersPage.open();
    }

    @When("they open the other customer's order")
    public void theyOpenTheOtherCustomersOrder() {
        orderPage.open(state.otherCustomersOrderId());
    }

    @When("they open the order and put the products back in the bag")
    public void theyOpenTheOrderAndRestoreTheBag() {
        orderPage.open(state.lastOrderId());
        orderPage.putProductsBackInBag();
    }

    @When("they put the products back in the bag")
    public void theyPutTheProductsBack() {
        orderPage.putProductsBackInBag();
    }

    @Then("the order page shows {string}")
    public void theOrderPageShows(String title) {
        orderPage.bannerShouldStartWith(title);
    }

    @Then("it lists {int} × {string}")
    public void itLists(int quantity, String product) {
        assertThat(orderPage.items()).contains(UiText.orderLine(quantity, product));
    }

    @Then("it shows the delivery address and {string} as the payment")
    public void itShowsTheAddressAndPayment(String payment) {
        var order = orders.get(state.lastOrderId());
        assertThat(orderPage.fact(UiText.DELIVER_TO_FACT)).isEqualTo(UiText.deliveryAddress(order.address()));
        assertThat(orderPage.fact(UiText.PAYMENT_FACT)).isEqualTo(payment);
    }

    @Then("the orders are listed newest first")
    public void theOrdersAreNewestFirst() {
        assertThat(ordersPage.orderIds()).containsExactlyElementsOf(state.orderIds().reversed());
    }

    @Then("My orders shows the order as confirmed")
    public void myOrdersShowsConfirmed() {
        ordersPage.open();
        ordersPage.statusShouldBe(state.lastOrderId(), UiText.ORDER_CONFIRMED_STATUS);
    }

    @Then("the payment for the order has failed")
    public void thePaymentFailed() {
        assertThat(orders.payment(state.lastOrderId()).status()).isEqualTo(ApiValues.PAYMENT_FAILED);
    }

    /** An order that lost the stock race is rejected before payment: no payment is ever attempted. */
    @Then("the payment for the order was never taken")
    public void thePaymentWasNeverTaken() {
        assertThat(orders.get(state.lastOrderId()).status()).isEqualTo(ApiValues.REJECTED);
        assertThat(orders.payment(state.lastOrderId()).status()).isEqualTo(ApiValues.PAYMENT_PENDING);
    }

    @Then("they see {string}")
    public void theySee(String message) {
        orderPage.notFoundMessageShouldBe(message);
    }

    /** The cancelled order gives its reservation back; the inventory service does that in the background. */
    @Then("the stock of {string} is the same as before the order")
    public void theStockIsTheSame(String productName) {
        var product = Catalog.byName(productName);
        var expected = state.stockBefore().get(product.id());
        var deadline = Instant.now().plus(Settings.current().orderTimeout());
        var stock = inventory.stock(product.id());
        while (stock != expected && Instant.now().isBefore(deadline)) {
            Poll.pause();
            stock = inventory.stock(product.id());
        }
        assertThat(stock).as("stock of %s after the failed payment released its reservation", product.name()).isEqualTo(expected);
    }
}
