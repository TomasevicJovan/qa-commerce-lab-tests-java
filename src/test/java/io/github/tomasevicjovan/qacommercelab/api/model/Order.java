package io.github.tomasevicjovan.qacommercelab.api.model;

import java.util.List;

/** An order as the API returns it. Only the fields the scenarios read are modelled. */
public record Order(
        String id,
        List<OrderLine> items,
        Address address,
        OrderPayment payment,
        int subtotal,
        int discount,
        int shipping,
        int total,
        String status,
        String reason) {

    public boolean isSettled() {
        return ApiValues.TERMINAL_STATUSES.contains(status);
    }
}
