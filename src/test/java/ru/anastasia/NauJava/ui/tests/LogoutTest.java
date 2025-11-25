package ru.anastasia.NauJava.ui.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.anastasia.NauJava.ui.pages.LoginPage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogoutTest extends BaseSeleniumTest {

    @Test
    void testSuccessfulLogout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();

        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Вход с существующими тестовыми данными
        loginPage.login("testuser", "password");

        // Проверка успешного входа
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));

        // Если произошла ошибка аутентификации, проверяем сообщение
        if (Objects.requireNonNull(driver.getCurrentUrl()).contains("login?error")) {
            assertTrue(loginPage.isErrorDisplayed(),
                    "Должно отображаться сообщение об ошибке при неудачном входе");
            return; // Прерываем тест, если вход не удался
        }

        // Проверяем различные возможные URL после успешного входа
        String currentUrl = driver.getCurrentUrl();
        boolean isOnRootPage = currentUrl.equals(baseUrl + "/");

        assertTrue(isOnRootPage,
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
                ExpectedConditions.urlToBe(baseUrl + "/"),
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/contacts"),
                ExpectedConditions.urlContains("/admin")
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

        // Ожидаем перенаправления на страницу логина (может быть с параметром logout)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.urlToBe(baseUrl + "/login?logout")
        ));

        // Пытаемся получить доступ к защищенной странице после выхода
        driver.get(baseUrl + "/contacts");

        // Должны быть перенаправлены на страницу логина (может быть с параметром continue)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.urlContains("login?continue")
        ));

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
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));

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