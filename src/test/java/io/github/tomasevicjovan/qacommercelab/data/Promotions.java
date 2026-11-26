package io.github.tomasevicjovan.qacommercelab.data;

/** Coupons and shipping rules from qa-commerce-lab docs/BUSINESS.md. Money in USD cents. */
public final class Promotions {

    /** 10% off the subtotal. */
    public static final String SAVE10 = "SAVE10";

    /** Free shipping from this amount, after all discounts. */
    public static final int FREE_SHIPPING_FROM = 15_000;

    public static final int SHIPPING_FEE = 700;

    private Promotions() {
    }
}
