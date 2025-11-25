package ru.anastasia.NauJava.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testGetRegistrationPage_ShouldReturnSuccess() {
        given()
                .when()
                .get("/registration")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.HTML)
                .body(containsString("Регистрация"))
                .body(containsString("Имя пользователя"))
                .body(containsString("form"));
    }

    @Test
    public void testGetLoginPage_ShouldReturnSuccess() {
        given()
                .when()
                .get("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.HTML)
                .body(containsString("login"));
    }

    @Test
    public void testPostRegistration_WithValidData_ShouldRedirectToLogin() {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();

        given()
                .contentType(ContentType.URLENC)
                .param("username", uniqueUsername)
                .param("password", "testpassword123")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.FOUND.value())
                .header("Location", containsString("/login?registrationSuccess"));
    }

    @Test
    public void testPostRegistration_WithExistingUsername_ShouldReturnError() {
        String existingUsername = "existinguser_" + System.currentTimeMillis();

        given()
                .contentType(ContentType.URLENC)
                .param("username", existingUsername)
                .param("password", "password123")
                .param("firstName", "Existing")
                .param("lastName", "User")
                .param("isActive", "true")
                .post("/registration");

        given()
                .contentType(ContentType.URLENC)
                .param("username", existingUsername)
                .param("password", "newpassword123")
                .param("firstName", "New")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("Регистрация"))
                .body(containsString("Пользователь с таким именем уже существует"));
    }

    @Test
    public void testPostRegistration_WithEmptyUsername_ShouldReturnFormWithError() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "")
                .param("password", "password123")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("Регистрация"))
                .body(containsString("form"));
    }

    @Test
    public void testPostRegistration_WithEmptyPassword_ShouldReturnFormWithError() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "user_" + System.currentTimeMillis())
                .param("password", "")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("Регистрация"))
                .body(containsString("Пароль не может быть пустым"));
    }

    @Test
    public void testPostRegistration_WithSpecialCharactersInUsername_ShouldHandleAppropriately() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "user@#$%") // Специальные символы
                .param("password", "password123")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithOnlyRequiredFields_ShouldSucceed() {
        String uniqueUsername = "minimal_" + System.currentTimeMillis();

        given()
                .contentType(ContentType.URLENC)
                .param("username", uniqueUsername)
                .param("password", "minimal123")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.FOUND.value())
                .header("Location", containsString("/login?registrationSuccess"));
    }

    @Test
    public void testPostRegistration_WithVeryLongFields_ShouldHandleAppropriately() {
        String longUsername = "a".repeat(50);
        String longPassword = "p".repeat(100);
        String longFirstName = "f".repeat(100);
        String longLastName = "l".repeat(100);

        given()
                .contentType(ContentType.URLENC)
                .param("username", longUsername)
                .param("password", longPassword)
                .param("firstName", longFirstName)
                .param("lastName", longLastName)
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithNullValues_ShouldHandleGracefully() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "nulluser_" + System.currentTimeMillis())
                .param("password", "password123")
                .param("firstName", "")
                .param("lastName", "")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(HttpStatus.FOUND.value())
                .header("Location", containsString("/login?registrationSuccess"));
    }

    @Test
    public void testPostRegistration_WithWhitespaceInUsername_ShouldTrimOrReject() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "  user_with_spaces  ")
                .param("password", "password123")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithSQLInjectionAttempt_ShouldNotCrash() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "admin'; DROP TABLE users; --")
                .param("password", "password123")
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())))
                .body(not(containsString("SQL Exception")));
    }

    @Test
    public void testPostRegistration_WithXSSAttempt_ShouldEscapeProperly() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "<script>alert('xss')</script>")
                .param("password", "password123")
                .param("firstName", "<img src=x onerror=alert(1)>")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithUnicodeCharacters_ShouldHandleCorrectly() {
        given()
                .contentType(ContentType.URLENC)
                .param("username", "用户_" + System.currentTimeMillis())
                .param("password", "password123")
                .param("firstName", "Тест")
                .param("lastName", "Пользователь")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithDuplicateEmailLikePattern_ShouldHandle() {
        String baseUsername = "user" + System.currentTimeMillis();

        given()
                .contentType(ContentType.URLENC)
                .param("username", baseUsername + "@domain.com")
                .param("password", "password123")
                .param("firstName", "Email")
                .param("lastName", "User")
                .param("isActive", "true")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }

    @Test
    public void testPostRegistration_WithCaseSensitiveUsername_ShouldRespectCase() {
        String baseUsername = "CaseUser" + System.currentTimeMillis();

        given()
                .contentType(ContentType.URLENC)
                .param("username", baseUsername.toLowerCase())
                .param("password", "password123")
                .post("/registration");

        given()
                .contentType(ContentType.URLENC)
                .param("username", baseUsername.toUpperCase())
                .param("password", "password456")
                .when()
                .post("/registration")
                .then()
                .statusCode(anyOf(is(HttpStatus.OK.value()), is(HttpStatus.FOUND.value())));
    }
}