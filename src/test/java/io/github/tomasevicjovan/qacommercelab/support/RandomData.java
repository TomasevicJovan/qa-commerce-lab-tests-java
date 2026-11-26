package io.github.tomasevicjovan.qacommercelab.support;

import io.github.tomasevicjovan.qacommercelab.api.model.Address;
import io.github.tomasevicjovan.qacommercelab.api.model.Credentials;
import net.datafaker.Faker;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Generated test data: every name, email, password and address a scenario types or sends comes from here.
 * The data is random but repeatable. The run has one seed (printed at the start); each scenario derives its own
 * from it and its name and line, so it gets the same data on any thread. Set TEST_SEED to repeat a run's data.
 * Only the unique part of an email changes between runs, so a repeated run never collides with an existing account.
 */
public final class RandomData {

    public static final long RUN_SEED = readRunSeed();

    private long seed;
    private Faker faker;

    /** Called by the hooks before the scenario's first step, with the scenario's name and line. */
    public void seedFor(String scenarioKey) {
        this.seed = RUN_SEED * 31 + scenarioKey.hashCode();
        this.faker = new Faker(Locale.ENGLISH, new Random(seed));
    }

    public long seed() {
        return seed;
    }

    public String fullName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    /** Unique email: the name part is repeatable, the 8-character suffix is unique per call. */
    public String email(String name) {
        var local = Arrays.stream(name.split(" "))
                .map(RandomData::asciiLetters)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("."));
        return "%s.%s@example.com".formatted(local.isEmpty() ? "qa" : local, UUID.randomUUID().toString().replace("-", "").substring(0, 8));
    }

    /** A password that meets the shop's rule (8 to 128 characters). */
    public String password(int length) {
        return faker.credentials().password(length, length, true, false, true);
    }

    public String password() {
        return password(12);
    }

    public String cardholder() {
        return fullName();
    }

    public Credentials credentials() {
        var name = fullName();
        return new Credentials(name, email(name), password());
    }

    public Address address() {
        return new Address(fullName(), faker.address().streetAddress(), faker.address().city(), faker.numerify("#####"), "BA");
    }

    private static long readRunSeed() {
        var value = System.getenv("TEST_SEED");
        try {
            if (value != null && Long.parseLong(value) > 0) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException ignored) {
            // fall through to a new seed
        }
        return ThreadLocalRandom.current().nextLong(1, Integer.MAX_VALUE);
    }

    private static String asciiLetters(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^A-Za-z]", "").toLowerCase(Locale.ROOT);
    }
}
