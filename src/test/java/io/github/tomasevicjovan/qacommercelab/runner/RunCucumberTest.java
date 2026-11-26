package io.github.tomasevicjovan.qacommercelab.runner;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Runs every feature file on the JUnit Platform. Parallel execution, the report and the @serial rule are
 * configured in src/test/resources/junit-platform.properties.
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class RunCucumberTest {
}
