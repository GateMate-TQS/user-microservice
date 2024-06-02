package testweb.cucumber;

import java.time.Duration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;

import io.cucumber.java.en.When;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import testweb.pages.IndexPage;
import testweb.pages.LoginPage;
import testweb.pages.Registerpage;
import java.util.concurrent.TimeUnit;


@ExtendWith(SeleniumJupiter.class)
public class UserSteps {
    private WebDriver driver;
    private WebDriverWait wait;
    private IndexPage indexPage;
    private LoginPage loginPage;
    private Registerpage registerPage;
    private String pass = "";

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
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

    @Given("the user is on the homepage")
    public void the_user_is_on_the_homepage() {
        indexPage = new IndexPage(driver);

        assertEquals("http://localhost/", driver.getCurrentUrl());

    }

    @When("the user clicks on the login button")
    public void the_user_clicks_on_the_login_button() {
        loginPage = indexPage.clickLogin();
    }

    @Then("the user is redirected to the login page")
    public void the_user_is_redirected_to_the_login_page() {
        assertEquals("http://localhost/login", driver.getCurrentUrl());
    }

    @Given("the user enters {string} as email")
    public void the_user_enters_as_email(String email) {
        loginPage.enterUsername(email);
    }
    
    @Given("the user enters {string} as password")
    public void the_user_enters_as_password(String password) {
        System.out.println("password: " + password);
        loginPage.enterPassword(password);
    }



    @When("the user clicks to login")
    public void the_user_clicks_on_the_submit_button_login() {
        try {
            loginPage.submit();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("the user loggedin and is redirected to the homepage")
    public void the_user_is_redirected_to_the_homepage() {
    
        assertEquals("http://localhost/", driver.getCurrentUrl());
    }

    @Given("the user doesnt have an account") 
    public void the_user_doesnt_have_an_account()
    {
        assertTrue(true);
    }

    @When("the user clicks in register account")
    public void the_user_clicks_on_the_register_link() {
        try {
            registerPage = loginPage.clickRegister();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("the user is redirected to the register page")
    public void the_user_is_redirected_to_the_register_page() {
        assertEquals("http://localhost/register", driver.getCurrentUrl());
    }


    @Given("the user enters {string} as register password") 
    public void the_user_enters_as_register_password(String password) {
        this.pass = password;
        registerPage.enterPassword(password);
    
    }

    @Given("the user enters {string} to confirm the password")
    public void the_user_enters_to_confirm_password(String confirmPassword) {
        registerPage.enterConfirmPassword(confirmPassword);
        assertEquals(this.pass, confirmPassword);
    }

    @Given("the user enters {string} as email to register")
    public void the_user_enters_as_email_register(String email) {
        registerPage.enterEmail(email);
    }

    @When("the user selects {string} as role") 
    public void the_user_selects_as_role(String role)
    {
        assertTrue(registerPage.assertRole(role));
    }

    @When("the user clicks to register")
    public void the_user_clicks_on_the_submit_button() {
        try {
            registerPage.submit();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("the user is registed and redirected to the login")
    public void the_user_is_registed_and_redirected_to_the_login() {
        assertEquals("http://localhost/login", driver.getCurrentUrl());
    }

}
