package ru.anastasia.NauJava.controller.page;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        log.warn("Обработка ошибки [статус: {}, путь: {}]", statusCode, path);

        model.addAttribute("status", statusCode);
        model.addAttribute("message", getErrorMessage(statusCode));
        model.addAttribute("timestamp", new Date());
        model.addAttribute("path", path);

        Object error = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (error != null) {
            model.addAttribute("error", error.toString());
        } else {
            Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if (errorMessage != null) {
                model.addAttribute("error", errorMessage.toString());
            }
        }

        return "error";
    }

    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Неверный запрос";
            case 401 -> "Неавторизованный доступ";
            case 403 -> "Доступ запрещен";
            case 404 -> "Страница не найдена";
            case 500 -> "Внутренняя ошибка сервера";
            default -> "Произошла ошибка";
        };
    }
}