package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.restassured.common.mapper.TypeRef;

import java.util.Map;

import static io.restassured.http.Method.GET;

/** Stock levels per product id. */
public final class InventoryClient extends BaseClient {

    public InventoryClient(ApiSession session) {
        super(session);
    }

    public int stock(String productId) {
        return send(GET, "/inventory", request(), new TypeRef<Map<String, Integer>>() { }, 200).getOrDefault(productId, 0);
    }
}
