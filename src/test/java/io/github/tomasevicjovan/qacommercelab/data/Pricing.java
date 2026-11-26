package io.github.tomasevicjovan.qacommercelab.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The shop's pricing rules written again, independently of the shop, from docs/BUSINESS.md:
 * coupon first, Mastercard second, each rounded half up on its own; shipping last, after discounts.
 * Used to send the expected total when an order is prepared through the API.
 */
public final class Pricing {

    private Pricing() {
    }

    public record Line(ProductInfo product, int quantity) {
    }

    public record Totals(int subtotal, int couponDiscount, int paymentDiscount, int shipping) {

        public int total() {
            return subtotal - couponDiscount - paymentDiscount + shipping;
        }
    }

    public static Totals calculate(List<Line> lines, String coupon, String cardBrand) {
        var subtotal = lines.stream().mapToInt(l -> l.product().price() * l.quantity()).sum();
        var couponDiscount = Promotions.SAVE10.equals(coupon) ? tenPercent(subtotal) : 0;
        var paymentDiscount = "mastercard".equals(cardBrand) ? tenPercent(subtotal - couponDiscount) : 0;
        var free = subtotal - couponDiscount - paymentDiscount >= Promotions.FREE_SHIPPING_FROM
                || lines.stream().allMatch(l -> l.product().freeShipping());
        return new Totals(subtotal, couponDiscount, paymentDiscount, free ? 0 : Promotions.SHIPPING_FEE);
    }

    private static int tenPercent(int cents) {
        return BigDecimal.valueOf(cents).multiply(BigDecimal.valueOf(10, 2)).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
