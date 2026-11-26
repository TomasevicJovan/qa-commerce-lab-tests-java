package io.github.tomasevicjovan.qacommercelab.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

/**
 * Test run settings, in order of precedence (later wins):
 * config.properties (defaults, in git), local.properties in the project folder (your machine, ignored by git),
 * environment variables (CI) and -D system properties. A key such as demoEmail is DEMO_EMAIL as an environment variable.
 */
public final class Settings {

    private static final Settings CURRENT = load();

    private final Properties properties;

    private Settings(Properties properties) {
        this.properties = properties;
    }

    public static Settings current() {
        return CURRENT;
    }

    public String baseUrl() {
        return value("baseUrl");
    }

    public Duration orderTimeout() {
        return Duration.ofSeconds(Long.parseLong(value("orderTimeoutSeconds")));
    }

    public boolean headless() {
        return Boolean.parseBoolean(value("headless"));
    }

    public int windowWidth() {
        return Integer.parseInt(value("windowWidth"));
    }

    public int windowHeight() {
        return Integer.parseInt(value("windowHeight"));
    }

    public Duration waitTimeout() {
        return Duration.ofSeconds(Long.parseLong(value("waitSeconds")));
    }

    /** The seeded demo account of qa-commerce-lab. Required only by the scenarios that use it. */
    public String demoEmail() {
        return required("demoEmail");
    }

    public String demoPassword() {
        return required("demoPassword");
    }

    private String value(String key) {
        var fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        var fromEnvironment = System.getenv(environmentName(key));
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment;
        }
        return properties.getProperty(key);
    }

    private String required(String key) {
        var value = value(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(("Setting '%s' is missing. Locally: add %s=<value> to local.properties (see README). "
                    + "In CI: add the repository secret %s.").formatted(key, key, environmentName(key)));
        }
        return value;
    }

    static String environmentName(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    private static Settings load() {
        var properties = new Properties();
        try (InputStream defaults = Settings.class.getResourceAsStream("/config.properties")) {
            properties.load(defaults);
            var local = Path.of("local.properties");
            if (Files.exists(local)) {
                try (Reader reader = Files.newBufferedReader(local)) {
                    properties.load(reader);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the test settings.", e);
        }
        return new Settings(properties);
    }
}
