package io.github.tomasevicjovan.qacommercelab.data;

/** A catalog product as the shop's business rules describe it. Prices in USD cents; compareAtPrice is null when there is no deal. */
public record ProductInfo(String id, String name, String subcategory, int price, Integer compareAtPrice, boolean freeShipping) {

    static ProductInfo product(String id, String name, String subcategory, int price) {
        return new ProductInfo(id, name, subcategory, price, null, false);
    }

    static ProductInfo onDeal(String id, String name, String subcategory, int price, int compareAtPrice) {
        return new ProductInfo(id, name, subcategory, price, compareAtPrice, false);
    }

    static ProductInfo shipsFree(String id, String name, String subcategory, int price) {
        return new ProductInfo(id, name, subcategory, price, null, true);
    }
}
