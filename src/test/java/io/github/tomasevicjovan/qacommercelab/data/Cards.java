package io.github.tomasevicjovan.qacommercelab.data;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/** Test cards from qa-commerce-lab docs/BUSINESS.md. Feature files refer to them by name. */
public final class Cards {

    public static final TestCard VISA = new TestCard("visa", "Visa", "4242 4242 4242 4242");
    public static final TestCard MASTERCARD = new TestCard("mastercard", "Mastercard", "5555 5555 5555 4444");

    /** A valid number that the simulated bank always declines. */
    public static final TestCard DECLINED = new TestCard("visa", "Visa", "4000 0000 0000 0002");

    private static final Map<String, TestCard> BY_NAME = Map.of("Visa", VISA, "Mastercard", MASTERCARD, "declined", DECLINED);

    private Cards() {
    }

    public static TestCard byName(String name) {
        var card = BY_NAME.get(name);
        if (card == null) {
            throw new IllegalArgumentException("No test card named '%s' in data/Cards.java.".formatted(name));
        }
        return card;
    }

    /** Expiry in MM/YY, the given number of months from now. */
    public static String expiryIn(int months) {
        return YearMonth.now(ZoneOffset.UTC).plusMonths(months).format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    public static String validExpiry() {
        return expiryIn(24);
    }
}
