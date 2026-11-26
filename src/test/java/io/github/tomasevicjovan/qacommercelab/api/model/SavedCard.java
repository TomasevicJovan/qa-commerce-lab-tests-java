package io.github.tomasevicjovan.qacommercelab.api.model;

public record SavedCard(String id, String holder, String brand, String last4, int expMonth, int expYear, boolean expired) {
}
