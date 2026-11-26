package io.github.tomasevicjovan.qacommercelab.pages.components;

/** What one product card shows. comparePrice is null without a deal; freeShippingMark is null when there is none. */
public record CardFacts(String name, String subcategory, int price, boolean salePrice, Integer comparePrice, String freeShippingMark) {
}
