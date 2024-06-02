package testweb.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class IndexPage {

    @FindBy(linkText = "Login")
    private WebElement loginButton;

    private WebDriver driver;

    public IndexPage(WebDriver driver) {
        String url = "http://localhost/";
        driver.get(url);
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }


    public LoginPage clickLogin() {
        loginButton.click();
        return new LoginPage(driver);
        
    }

     

}