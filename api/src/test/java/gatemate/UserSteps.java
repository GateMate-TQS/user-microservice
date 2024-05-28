package gatemate;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(SeleniumJupiter.class)
public class UserSteps {
    private WebDriver driver;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup(); // Make sure to setup ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--ignore-certificate-errors");
        options.setAcceptInsecureCerts(true);
        options.addArguments("--allow-insecure-localhost");
        driver = new ChromeDriver(options);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @When("I navigate to {string}")
    public void i_am_on_the_blaze_demo_home_page(String url) {
        System.out.println("Navigating to: " + url);

        driver.get(url);
    }
}
