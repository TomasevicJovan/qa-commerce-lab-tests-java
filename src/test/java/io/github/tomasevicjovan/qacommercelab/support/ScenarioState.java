package io.github.tomasevicjovan.qacommercelab.support;

import io.github.tomasevicjovan.qacommercelab.api.model.Credentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * What one scenario knows about its own customer and orders. PicoContainer creates one per scenario and
 * passes it to every step class that asks for it, so scenarios running in parallel never share state.
 */
public final class ScenarioState {

    private Credentials credentials;
    private String otherCustomersOrderId;
    private final List<String> orderIds = new ArrayList<>();
    private final Map<String, Integer> stockBefore = new HashMap<>();

    public Credentials credentials() {
        return credentials;
    }

    public void credentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public List<String> orderIds() {
        return orderIds;
    }

    public String lastOrderId() {
        if (orderIds.isEmpty()) {
            throw new IllegalStateException("No order was placed in this scenario.");
        }
        return orderIds.getLast();
    }

    public String otherCustomersOrderId() {
        return otherCustomersOrderId;
    }

    public void otherCustomersOrderId(String orderId) {
        this.otherCustomersOrderId = orderId;
    }

    public Map<String, Integer> stockBefore() {
        return stockBefore;
    }
}
