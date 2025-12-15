package ru.anastasia.NauJava.ui.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    public BasePage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.baseUrl = baseUrl;
        PageFactory.initElements(driver, this);
    }

    protected void waitForPageLoad() {
        wait.until(webDriver -> {
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            return "complete".equals(js.executeScript("return document.readyState"));
        });
    }
}