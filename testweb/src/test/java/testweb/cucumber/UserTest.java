package testweb.cucumber;

import static io.cucumber.junit.platform.engine.Constants.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("testweb/cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "testweb/cucumber")
class UserTest {

    @Disabled("This test is disabled because it's not implemented yet")
    @Test
    void testPalha() {
        Assertions.assertTrue(true);
    }
}
