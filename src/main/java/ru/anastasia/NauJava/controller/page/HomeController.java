package ru.anastasia.NauJava.controller.page;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.anastasia.NauJava.dto.DashboardStats;

import java.util.Collections;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("currentPage", "home");

        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }

        DashboardStats stats = DashboardStats.builder()
                .contactsCount(0L)
                .companiesCount(0L)
                .favoritesCount(0L)
                .upcomingBirthdays(0L)
                .build();

        model.addAttribute("stats", stats);
        model.addAttribute("recentActivities", Collections.emptyList());

        return "index";
    }
}