package testweb.pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;

public class LoginPage {

    private WebDriver driver;

    @FindBy(xpath="//input[@id='email']")
    private WebElement usernameField;

    @FindBy(xpath="//div[2]/input")
    private WebElement passwordField;

    @FindBy(xpath="//div[@id='root']/div/div[2]/form/div[4]/button")
    private WebElement submitButton;

    @FindBy(xpath="//a[contains(text(),'Register here')]")
    private WebElement registerLink;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void enterUsername(String username) {
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordField.sendKeys(password);
    }

    public void submit() {
        submitButton.click();
    }

    public Registerpage clickRegister() {
        registerLink.click();
        return new Registerpage(driver);
    }
   

}
