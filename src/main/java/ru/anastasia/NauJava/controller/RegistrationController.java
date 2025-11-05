package ru.anastasia.NauJava.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.anastasia.NauJava.entity.user.User;
import ru.anastasia.NauJava.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute User user, Model model) {
        try {
            if (userService.userExists(user.getUsername())) {
                model.addAttribute("message", "Пользователь с таким именем уже существует");
                return "registration";
            }

            user.setIsActive(true);
            userService.createUser(user);
            return "redirect:/login?registrationSuccess";

        } catch (Exception ex) {
            model.addAttribute("message", "Ошибка при регистрации: " + ex.getMessage());
            return "registration";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}