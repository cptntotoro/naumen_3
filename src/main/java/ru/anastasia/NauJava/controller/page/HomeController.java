package ru.anastasia.NauJava.controller.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.anastasia.NauJava.service.stats.StatisticsService;

import java.util.Collections;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    /**
     * Сервис статистики дашбордов
     */
    private final StatisticsService statisticsService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        log.debug("GET / - главная страница");

        model.addAttribute("currentPage", "home");

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            log.debug("Пользователь аутентифицирован: {}", username);
        } else {
            log.debug("Анонимный доступ к главной странице");
        }

        try {
            model.addAttribute("stats", statisticsService.getUserDashboardStats());
            model.addAttribute("recentActivities", Collections.emptyList());
            log.debug("Данные для дашборда загружены");
        } catch (Exception e) {
            log.error("Ошибка при загрузке статистики для дашборда", e);
            model.addAttribute("stats", Collections.emptyMap());
            model.addAttribute("recentActivities", Collections.emptyList());
        }

        return "index";
    }
}