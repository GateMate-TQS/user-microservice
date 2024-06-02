package testweb.pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class Registerpage {

    private WebDriver driver;

    @FindBy(xpath="//input[@id='email']")
    private WebElement email_;

    @FindBy(xpath="//input[@id='password']")
    private WebElement password_;

    @FindBy(xpath="//input[@id='confirmPassword']")
    private WebElement confirmPassword_;

    @FindBy(xpath="//button[@type='submit']")
    private WebElement submitButton;

    @FindBy(xpath="//select[@id='role']")
    private WebElement roleDropdown;

    public Registerpage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void enterEmail(String email) {
        email_.sendKeys(email);
    }

    public void enterPassword(String password) {
        password_.sendKeys(password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        confirmPassword_.sendKeys(confirmPassword);
    }

    public boolean assertRole(String role) {
        System.out.println("Selected role: " + getSelectedRole());
        return getSelectedRole().equals(role);
    }
    public String getSelectedRole() {
        Select dropdown = new Select(roleDropdown);
        return dropdown.getFirstSelectedOption().getText();
    }

    public void submit() {
        submitButton.click();
    }


}
