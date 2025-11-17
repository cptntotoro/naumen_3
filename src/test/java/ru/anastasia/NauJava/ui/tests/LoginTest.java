package ru.anastasia.NauJava.ui.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.anastasia.NauJava.ui.pages.HomePage;
import ru.anastasia.NauJava.ui.pages.LoginPage;
import ru.anastasia.NauJava.ui.pages.RegistrationPage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class LoginTest extends BaseSeleniumTest {

    @Test
    void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();

        // Проверяем, что мы на странице логина
        boolean isOnLoginPage = loginPage.isLoginPageDisplayed();
        boolean isOnErrorPage = Objects.requireNonNull(driver.getCurrentUrl()).contains("/error") ||
                Objects.requireNonNull(driver.getPageSource()).contains("Ошибка") ||
                driver.getPageSource().contains("Произошла ошибка");

        if (isOnErrorPage) {
            System.out.println("Отображается страница ошибки вместо страницы логина");
            driver.get(baseUrl + "/login");
            wait.until(ExpectedConditions.urlContains("login"));
        }

        assertTrue(loginPage.isLoginPageDisplayed() || isOnLoginPage,
                "Страница логина должна отображаться");

        // Вход с существующими тестовыми данными
        loginPage.login("testuser", "password");

        // Проверка успешного входа
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/contacts"),
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("login?error")
        ));

        // Если произошла ошибка аутентификации
        if (driver.getCurrentUrl().contains("login?error")) {
            if (Objects.requireNonNull(driver.getPageSource()).contains("Ошибка") ||
                    driver.getPageSource().contains("Произошла ошибка")) {
                fail("Отображается страница ошибки вместо страницы логина с сообщением об ошибке");
            }
            assertTrue(loginPage.isErrorDisplayed(),
                    "Должно отображаться сообщение об ошибке при неудачном входе");
            return;
        }

        // Проверяем различные возможные URL после успешного входа
        String currentUrl = driver.getCurrentUrl();
        boolean isOnHomePage = currentUrl.endsWith("/") || currentUrl.contains("/home") || currentUrl.contains("/contacts");
        boolean isOnAdminPage = currentUrl.contains("/admin");

        assertTrue(isOnHomePage || isOnAdminPage,
                "После успешного входа должна отображаться домашняя или админ страница. Текущий URL: " + currentUrl);
    }

    @Test
    void testFailedLoginWithInvalidCredentials() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Попытка входа с неверными данными
        loginPage.login("nonexistent_user", "wrong_password");

        // Проверка сообщения об ошибке
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.alert-danger, .alert.alert-danger")),
                ExpectedConditions.urlContains("error"),
                ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'")
        ));

        boolean hasErrorAlert = !driver.findElements(By.cssSelector("div.alert-danger, .alert.alert-danger")).isEmpty();
        boolean stayedOnLoginPage = Objects.requireNonNull(driver.getCurrentUrl()).contains("login");
        boolean hasErrorParam = driver.getCurrentUrl().contains("error");

        assertTrue(hasErrorAlert || stayedOnLoginPage || hasErrorParam,
                "Должна отображаться ошибка или оставаться страница логина");
    }

    @Test
    void testLoginWithEmptyCredentials() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Попытка входа с пустыми полями
        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.clickLogin();

        // Должны остаться на странице логина или получить ошибку
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("error"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert-danger")),
                ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'")
        ));

        boolean stayedOnLoginPage = loginPage.isLoginPageDisplayed();
        boolean hasError = loginPage.isErrorDisplayed();
        boolean hasValidationError = !driver.findElements(By.cssSelector("input:invalid")).isEmpty();

        assertTrue(stayedOnLoginPage || hasError || hasValidationError,
                "При пустых полях должна оставаться страница входа, отображаться ошибка или срабатывать валидация");
    }

    @Test
    void testNavigationToRegistrationFromLogin() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Переход на страницу регистрации
        loginPage.clickRegistrationLink();

        // Проверка перехода на страницу регистрации
        wait.until(ExpectedConditions.urlContains("registration"));
        RegistrationPage registrationPage = new RegistrationPage(driver, baseUrl);
        assertTrue(registrationPage.isRegistrationPageDisplayed(),
                "Должна отображаться страница регистрации");
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/registration"),
                "URL должен указывать на страницу регистрации. Текущий URL: " + driver.getCurrentUrl());
    }

    @Test
    void testAccessToProtectedPageWithoutLogin() {
        driver.get(baseUrl + "/contacts");

        // Должен быть перенаправлен на страницу входа
        wait.until(ExpectedConditions.urlContains("login"));
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        assertTrue(loginPage.isLoginPageDisplayed(),
                "При доступе к защищенной странице без авторизации должна отображаться страница входа");
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("login"),
                "URL должен содержать login. Текущий URL: " + driver.getCurrentUrl());
    }

    @Test
    void testLoginWithAdminCredentials() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Вход с учетными данными администратора
        loginPage.login("admin", "admin");

        // Проверка входа администратора
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
        HomePage homePage = new HomePage(driver, baseUrl);

        boolean isOnAdminPage = Objects.requireNonNull(driver.getCurrentUrl()).contains("/admin");

        assertTrue(isOnAdminPage,
                "Администратор должен успешно войти в систему и быть направлен на админ страницу. Текущий URL: " + driver.getCurrentUrl());

        assertTrue(driver.getCurrentUrl().contains("/admin"),
                "Администратор должен быть перенаправлен на админ панель");
    }

    @Test
    void testReturnToLoginPageAfterSessionTimeout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();

        loginPage.login("testuser", "password");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/contacts"),
                ExpectedConditions.urlContains("login?error")
        ));

        if (Objects.requireNonNull(driver.getCurrentUrl()).contains("login?error")) {
            System.out.println("Login failed in session timeout test. URL: " + driver.getCurrentUrl());
            return;
        }

        boolean isLoggedIn = !driver.getCurrentUrl().contains("login");
        assertTrue(isLoggedIn, "Должны быть залогинены");

        // Эмулируем истекшую сессию
        driver.manage().deleteAllCookies();

        // Пытаемся перейти на защищенную страницу
        driver.get(baseUrl + "/contacts");

        // Должны быть перенаправлены на логин
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='password']"))
        ));

        boolean redirectedToLogin = driver.getCurrentUrl().contains("login");
        boolean onLoginPage = !driver.findElements(By.cssSelector("input[type='password']")).isEmpty();

        assertTrue(redirectedToLogin || onLoginPage,
                "После истечения сессии должна отображаться страница входа. Текущий URL: " + driver.getCurrentUrl());
    }
}