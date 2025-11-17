package ru.anastasia.NauJava.controller.page;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.anastasia.NauJava.service.stats.StatisticsService;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class HomeController {

    /**
     * Сервис статистики дашбордов
     */
    private final StatisticsService statisticsService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("currentPage", "home");

        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }

        model.addAttribute("stats", statisticsService.getUserDashboardStats());
        model.addAttribute("recentActivities", Collections.emptyList());

        return "index";
    }
}