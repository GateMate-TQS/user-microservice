package testweb.cucumber;

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
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.setAcceptInsecureCerts(true);
            
            driver = new ChromeDriver(options);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage());
        }
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
