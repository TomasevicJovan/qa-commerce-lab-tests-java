package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.CartItem;
import io.github.tomasevicjovan.qacommercelab.api.model.CartRequest;
import io.restassured.common.mapper.TypeRef;

import java.util.List;

import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.PUT;

/** The signed-in customer's bag. */
public final class CartClient extends BaseClient {

    public CartClient(ApiSession session) {
        super(session);
    }

    public List<CartItem> get() {
        return send(GET, "/cart", request(), new TypeRef<List<CartItem>>() { }, 200);
    }

    /** Replaces the whole bag. */
    public void set(List<CartItem> items) {
        sendIgnoringBody(PUT, "/cart", request().body(new CartRequest(items)), 200);
    }
}
