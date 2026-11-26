package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.CheckoutRequest;
import io.github.tomasevicjovan.qacommercelab.api.model.Order;
import io.github.tomasevicjovan.qacommercelab.api.model.Payment;
import io.github.tomasevicjovan.qacommercelab.support.Poll;
import io.github.tomasevicjovan.qacommercelab.support.Settings;
import io.restassured.common.mapper.TypeRef;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;

/** Orders and their payments. Orders are accepted (202) and processed in the background. */
public final class OrdersClient extends BaseClient {

    public OrdersClient(ApiSession session) {
        super(session);
    }

    public Order place(CheckoutRequest checkout) {
        return send(POST, "/orders", request().header("Idempotency-Key", UUID.randomUUID().toString()).body(checkout), Order.class, 202);
    }

    public Order get(String orderId) {
        return send(GET, "/orders/" + orderId, request(), Order.class, 200);
    }

    public List<Order> list() {
        return send(GET, "/orders", request(), new TypeRef<List<Order>>() { }, 200);
    }

    public Payment payment(String orderId) {
        return send(GET, "/payments/" + orderId, request(), Payment.class, 200);
    }

    /** Polls an order until it reaches a final state: confirmed, rejected or cancelled. */
    public Order settled(String orderId) {
        var deadline = Instant.now().plus(Settings.current().orderTimeout());
        while (true) {
            var order = get(orderId);
            if (order.isSettled()) {
                return order;
            }
            if (Instant.now().isAfter(deadline)) {
                throw new IllegalStateException("Order %s is still '%s' after %ss.".formatted(orderId, order.status(), Settings.current().orderTimeout().toSeconds()));
            }
            Poll.pause();
        }
    }

}
