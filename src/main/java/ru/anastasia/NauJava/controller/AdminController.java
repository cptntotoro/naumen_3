package ru.anastasia.NauJava.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.service.stats.StatisticsService;

@Controller
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * Сервис статистики дашбордов
     */
    private final StatisticsService statisticsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPanel(Model model) {
        model.addAttribute("stats", statisticsService.getAdminDashboardStats());
        model.addAttribute("message", "Панель администратора");
        return "admin";
    }

    @GetMapping("/swagger")
    @PreAuthorize("hasRole('ADMIN')")
    public String swaggerRedirect() {
        return "redirect:/swagger-ui/index.html";
    }
}