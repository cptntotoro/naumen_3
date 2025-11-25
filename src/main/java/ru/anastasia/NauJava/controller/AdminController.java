package ru.anastasia.NauJava.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.service.stats.StatisticsService;

@Slf4j
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
        log.info("GET /admin - доступ к панели администратора");

        try {
            model.addAttribute("stats", statisticsService.getAdminDashboardStats());
            model.addAttribute("message", "Панель администратора");
            log.debug("Данные для админ-панели загружены");
            return "admin";
        } catch (Exception e) {
            log.error("Ошибка при загрузке админ-панели", e);
            throw e;
        }
    }

    @GetMapping("/swagger")
    @PreAuthorize("hasRole('ADMIN')")
    public String swaggerRedirect() {
        log.debug("GET /admin/swagger - редирект на Swagger UI");
        return "redirect:/swagger-ui/index.html";
    }
}