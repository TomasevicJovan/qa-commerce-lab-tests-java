package io.github.tomasevicjovan.qacommercelab.api.model;

public record OrderLine(String productId, int quantity, String name, int unitPrice, boolean freeShipping) {
}
