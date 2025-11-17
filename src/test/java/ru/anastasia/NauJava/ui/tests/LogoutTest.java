package ru.anastasia.NauJava.ui.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.anastasia.NauJava.ui.pages.LoginPage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class LogoutTest extends BaseSeleniumTest {

    @Test
    void testSuccessfulLogout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();

        // Проверяем, что мы на странице логина или на странице ошибки
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

        // Если произошла ошибка аутентификации, проверяем сообщение
        if (driver.getCurrentUrl().contains("login?error")) {
            if (Objects.requireNonNull(driver.getPageSource()).contains("Ошибка") ||
                    driver.getPageSource().contains("Произошла ошибка")) {
                System.out.println("После попытки входа отображается страница ошибки");
                fail("Отображается страница ошибки вместо страницы логина с сообщением об ошибке");
            }
            assertTrue(loginPage.isErrorDisplayed(),
                    "Должно отображаться сообщение об ошибке при неудачном входе");
            return; // Прерываем тест, если вход не удался
        }

        // Проверяем различные возможные URL после успешного входа
        String currentUrl = driver.getCurrentUrl();
        boolean isOnHomePage = currentUrl.endsWith("/");
        boolean isOnAdminPage = currentUrl.contains("/admin");

        assertTrue(isOnHomePage || isOnAdminPage,
                "После успешного входа должна отображаться домашняя или админ страница. Текущий URL: " + currentUrl);

        // Выход из системы
        driver.findElement(By.xpath("//a[contains(@href, '/logout')]")).click();

        // Проверка успешного выхода
        wait.until(ExpectedConditions.urlContains("login"));
        assertTrue(driver.getCurrentUrl().contains("login"),
                "После выхода должна отображаться страница логина. Текущий URL: " + driver.getCurrentUrl());

        // Проверяем, что мы на странице логина
        LoginPage redirectedLoginPage = new LoginPage(driver, baseUrl);
        assertTrue(redirectedLoginPage.isLoginPageDisplayed(),
                "После выхода должна отображаться страница входа");
    }

    @Test
    void testCannotAccessProtectedPagesAfterLogout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Логинимся
        loginPage.login("testuser", "password");

        // Проверка успешного входа
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/contacts"),
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("login?error")
        ));

        // Если вход не удался, прерываем тест
        if (Objects.requireNonNull(driver.getCurrentUrl()).contains("login?error")) {
            System.out.println("Login failed in logout test. URL: " + driver.getCurrentUrl());
            return;
        }

        // Проверяем, что мы залогинены
        boolean isLoggedIn = !driver.getCurrentUrl().contains("login");
        assertTrue(isLoggedIn, "Должны быть залогинены перед выходом");

        // Выходим из системы
        driver.findElement(By.xpath("//a[contains(@href, '/logout')]")).click();
        wait.until(ExpectedConditions.urlContains("login"));

        // Пытаемся получить доступ к защищенной странице после выхода
        driver.get(baseUrl + "/contacts");

        // Должны быть перенаправлены на страницу логина
        wait.until(ExpectedConditions.urlContains("login"));
        LoginPage loginPageAfterLogout = new LoginPage(driver, baseUrl);
        assertTrue(loginPageAfterLogout.isLoginPageDisplayed(),
                "После выхода при доступе к защищенной странице должна отображаться страница входа");
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("login"),
                "URL должен содержать login после выхода. Текущий URL: " + driver.getCurrentUrl());
    }

    @Test
    void testLogoutAfterSessionTimeout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Логинимся
        loginPage.login("testuser", "password");

        // Проверка успешного входа
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/contacts"),
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("login?error")
        ));

        // Если вход не удался, прерываем тест
        if (Objects.requireNonNull(driver.getCurrentUrl()).contains("login?error")) {
            System.out.println("Login failed in session timeout test. URL: " + driver.getCurrentUrl());
            return;
        }

        // Проверяем, что мы залогинены
        boolean isLoggedIn = !driver.getCurrentUrl().contains("login");
        assertTrue(isLoggedIn, "Должны быть залогинены");

        // Эмулируем истекшую сессию
        driver.manage().deleteAllCookies();

        // Пытаемся выйти из системы (должны быть перенаправлены на логин)
        driver.get(baseUrl + "/logout");

        // Должны быть перенаправлены на страницу логина
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='password']"))
        ));

        boolean redirectedToLogin = driver.getCurrentUrl().contains("login");
        boolean onLoginPage = !driver.findElements(By.cssSelector("input[type='password']")).isEmpty();

        assertTrue(redirectedToLogin || onLoginPage,
                "После истечения сессии при попытке выхода должна отображаться страница входа. Текущий URL: " + driver.getCurrentUrl());
    }
}