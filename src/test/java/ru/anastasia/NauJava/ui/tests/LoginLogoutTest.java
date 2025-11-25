package ru.anastasia.NauJava.ui.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.anastasia.NauJava.ui.pages.LoginPage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginLogoutTest extends BaseSeleniumTest {

    @Test
    void testSuccessfulLoginAndLogout() {
        LoginPage loginPage = new LoginPage(driver, baseUrl);
        loginPage.navigateTo();

        // Проверяем, что мы на странице логина
        assertTrue(loginPage.isLoginPageDisplayed(), "Страница логина должна отображаться");

        // Вход с существующими тестовыми данными
        loginPage.login("testuser", "password");

        // Проверка успешного входа - ожидаем корневую страницу
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
    }
}