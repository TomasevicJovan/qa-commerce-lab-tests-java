package io.github.tomasevicjovan.qacommercelab.api.clients;

import static io.restassured.http.Method.PUT;

public final class WishlistClient extends BaseClient {

    public WishlistClient(ApiSession session) {
        super(session);
    }

    public void add(String productId) {
        sendIgnoringBody(PUT, "/wishlist/" + productId, request(), 200);
    }
}
