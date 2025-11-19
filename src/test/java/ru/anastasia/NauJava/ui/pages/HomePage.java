package ru.anastasia.NauJava.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    @FindBy(xpath = "//h1[contains(., 'Главная')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//a[contains(@href, '/logout')]")
    private WebElement logoutLink;

    @FindBy(css = "div.stats-grid")
    private WebElement statsSection;

    @FindBy(css = "section.dashboard-actions")
    private WebElement actionsSection;

    @FindBy(xpath = "//a[contains(@href, '/contacts') and contains(@class, 'nav-link')]")
    private WebElement contactsNavLink;

    @FindBy(xpath = "//span[contains(@class, 'brand-text') and contains(text(), 'Contact Manager')]")
    private WebElement brandLogo;

    public HomePage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
        PageFactory.initElements(driver, this);
    }

    public boolean isHomePageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.isDisplayed() && driver.getCurrentUrl().contains("/");
        } catch (Exception e) {
            return false;
        }
    }
}
