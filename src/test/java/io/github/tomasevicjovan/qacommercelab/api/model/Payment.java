package io.github.tomasevicjovan.qacommercelab.api.model;

public record Payment(String orderId, String status, Integer amount, String reason) {
}
