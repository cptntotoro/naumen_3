package ru.anastasia.NauJava.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.service.user.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    @GetMapping("/registration")
    public String registration() {
        log.debug("GET /registration - форма регистрации");
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute User user, Model model) {
        log.info("POST /registration - регистрация пользователя [username: {}]", user.getUsername());

        try {
            User createdUser = userService.registerUser(user);
            log.info("Пользователь успешно зарегистрирован [ID: {}, username: {}]",
                    createdUser.getId(), createdUser.getUsername());
            return "redirect:/login?registrationSuccess";

        } catch (IllegalArgumentException ex) {
            log.warn("Ошибка валидации при регистрации пользователя [username: {}]: {}",
                    user.getUsername(), ex.getMessage());
            model.addAttribute("message", ex.getMessage());
            return "registration";
        } catch (Exception ex) {
            log.error("Ошибка при регистрации пользователя [username: {}]", user.getUsername(), ex);
            model.addAttribute("message", "Ошибка при регистрации: " + ex.getMessage());
            return "registration";
        }
    }

    @GetMapping("/login")
    public String login() {
        log.debug("GET /login - форма входа");
        return "login";
    }
}