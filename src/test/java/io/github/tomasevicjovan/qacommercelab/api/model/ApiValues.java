package io.github.tomasevicjovan.qacommercelab.api.model;

import java.util.Set;

/** Values the API uses for payment methods, order and payment states and lab switches (docs/BUSINESS.md). */
public final class ApiValues {

    public static final String CARD = "card";
    public static final String TEST_WALLET = "test-wallet";

    public static final String CONFIRMED = "confirmed";
    public static final String REJECTED = "rejected";
    public static final String CANCELLED = "cancelled";
    public static final Set<String> TERMINAL_STATUSES = Set.of(CONFIRMED, REJECTED, CANCELLED);

    public static final String PAYMENT_PENDING = "pending";
    public static final String PAYMENT_FAILED = "failed";

    public static final String BANK_DECLINES = "decline";

    private ApiValues() {
    }
}
