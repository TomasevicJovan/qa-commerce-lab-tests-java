package io.github.tomasevicjovan.qacommercelab.support;

import io.github.tomasevicjovan.qacommercelab.api.clients.ApiSession;
import io.github.tomasevicjovan.qacommercelab.api.clients.LabClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public final class Hooks {

    private static final String SERIAL = "@serial";

    private final Browser browser;
    private final RandomData random;
    private final LabClient lab;

    public Hooks(Browser browser, RandomData random, LabClient lab) {
        this.browser = browser;
        this.random = random;
        this.lab = lab;
    }

    /** Fails the whole run early, with one clear message, when the lab is not up. */
    @BeforeAll
    public static void checkLabAndPrintSeed() {
        System.out.printf("Test data seed: %d (set TEST_SEED=%d to repeat this run's data)%n", RandomData.RUN_SEED, RandomData.RUN_SEED);
        var lab = new LabClient(new ApiSession());
        try {
            lab.status();
            lab.restoreDefaults();
        } catch (RuntimeException e) {
            throw new IllegalStateException("qa-commerce-lab is not reachable at %s. Start it first (see README). %s"
                    .formatted(Settings.current().baseUrl(), e.getMessage()), e);
        }
    }

    /** Runs before anything else: seeds the scenario's random data from the run seed and the scenario's name and line. */
    @Before(order = 0)
    public void seedRandomData(Scenario scenario) {
        random.seedFor(scenario.getName() + ":" + scenario.getLine());
        scenario.log("Test data seed: run %d, scenario %d".formatted(RandomData.RUN_SEED, random.seed()));
    }

    /** Attaches a screenshot of the browser window to a failed scenario, in the HTML report. */
    @After(order = 20)
    public void screenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            scenario.attach(browser.screenshot(), "image/png", "Browser window when the scenario failed");
        }
    }

    /** Puts the lab back to its baseline after a scenario that changed global switches (they run alone, see junit-platform.properties). */
    @After(value = SERIAL, order = 0)
    public void restoreLab() {
        lab.restoreDefaults();
    }
}
