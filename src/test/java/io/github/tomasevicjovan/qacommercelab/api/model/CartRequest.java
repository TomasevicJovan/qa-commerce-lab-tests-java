package io.github.tomasevicjovan.qacommercelab.api.model;

import java.util.List;

/** Body of PUT /api/cart: the whole bag at once. */
public record CartRequest(List<CartItem> items) {
}
