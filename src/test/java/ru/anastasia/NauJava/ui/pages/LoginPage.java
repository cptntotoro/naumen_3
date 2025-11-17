package ru.anastasia.NauJava.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.anastasia.NauJava.ui.data.TestUsers;

import java.util.Objects;

public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[contains(., 'Войти')]")
    private WebElement loginButton;

    @FindBy(css = "div.alert-danger")
    private WebElement errorAlert;

    @FindBy(css = "div.alert-success")
    private WebElement successAlert;

    @FindBy(xpath = "//h1[contains(., 'Вход')]")
    private WebElement pageTitle;

    @FindBy(linkText = "Зарегистрироваться")
    private WebElement registrationLink;

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public LoginPage navigateTo() {
        driver.get(baseUrl + "/login");
        waitForPageLoad();
        return this;
    }

    public LoginPage enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public HomePage loginAs(TestUsers user) {
        return enterUsername(user.getUsername())
                .enterPassword(user.getPassword())
                .clickLoginAndGoToHomePage();
    }

    public HomePage clickLoginAndGoToHomePage() {
        clickLogin();
        return new HomePage(driver, baseUrl);
    }

    public boolean isErrorDisplayed() {
        try {
            return errorAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.isDisplayed() &&
                    Objects.requireNonNull(driver.getCurrentUrl()).contains("/login") &&
                    usernameInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public RegistrationPage clickRegistrationLink() {
        registrationLink.click();
        return new RegistrationPage(driver, baseUrl);
    }

    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorAlert));
            return errorAlert.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
