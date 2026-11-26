package io.github.tomasevicjovan.qacommercelab.api.model;

/** Card details as a customer types them. The lab never stores or returns the full number or the CVC. */
public record NewCard(String holder, String number, String expiry, String cvc) {
}
