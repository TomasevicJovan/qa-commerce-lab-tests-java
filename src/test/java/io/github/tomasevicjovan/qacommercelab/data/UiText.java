package io.github.tomasevicjovan.qacommercelab.data;

import io.github.tomasevicjovan.qacommercelab.api.model.Address;

/**
 * Fixed texts of the storefront that step definitions check. Texts a scenario is about are written in
 * the feature files; this class holds the ones the steps need on their own. Source: docs/BUSINESS.md.
 */
public final class UiText {

    public static final String SIGN_IN = "Sign in";
    public static final String NO_PRODUCTS_FOUND = "No products found. Try another search or category.";
    public static final String PAYMENT_SUCCESSFUL = "Payment successful";
    public static final String PAYMENT_FACT = "Payment";
    public static final String DELIVER_TO_FACT = "Deliver to";
    public static final String BAG_HEADING = "Your shopping bag";

    /** Order status as My orders shows it. */
    public static final String ORDER_CONFIRMED_STATUS = "confirmed";

    // Rows of the totals box (bag, checkout and order page).
    public static final String SUBTOTAL = "Subtotal";
    public static final String DISCOUNT = "Discount";
    public static final String SHIPPING = "Shipping";
    public static final String TOTAL = "Total";

    private UiText() {
    }

    /** The account button of a signed-in customer. */
    public static String accountButton(String name) {
        return name + " ▾";
    }

    /** A line on the order page, for example "2 × Still Ceramic Mug". */
    public static String orderLine(int quantity, String productName) {
        return quantity + " × " + productName;
    }

    /** The delivery address as the order page shows it, one part per line. */
    public static String deliveryAddress(Address a) {
        return "%s\n%s\n%s %s, %s".formatted(a.name(), a.line1(), a.postalCode(), a.city(), a.country());
    }

}
