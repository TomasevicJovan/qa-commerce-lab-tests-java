package io.github.tomasevicjovan.qacommercelab.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Body of POST /api/orders. {@code expectedTotal} is the total the customer saw; the lab refuses the order
 * if its own calculation differs. Money is always integer USD cents.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CheckoutRequest(
        Address address,
        String paymentMethod,
        String savedCardId,
        NewCard card,
        Boolean saveCard,
        String coupon,
        int expectedTotal) {

    public static CheckoutRequest withWallet(Address address, int expectedTotal) {
        return new CheckoutRequest(address, ApiValues.TEST_WALLET, null, null, null, null, expectedTotal);
    }

    public static CheckoutRequest withNewCard(Address address, NewCard card, int expectedTotal) {
        return new CheckoutRequest(address, ApiValues.CARD, null, card, false, null, expectedTotal);
    }
}
