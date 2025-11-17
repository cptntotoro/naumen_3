package ru.anastasia.NauJava.controller.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.entity.report.Report;
import ru.anastasia.NauJava.entity.report.ReportStatus;
import ru.anastasia.NauJava.service.report.ReportService;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    /**
     * Сервис отчетов
     */
    private final ReportService reportService;

    @GetMapping
    public String createAndRedirect() {
        Report report = reportService.createReport();
        reportService.generateReportAsync(report.getId());
        return "redirect:/reports/" + report.getId();
    }

    @GetMapping("/{id}")
    public String getReport(@PathVariable Long id, Model model) {
        Report report = reportService.getReport(id);

        model.addAttribute("reportId", id);

        if (report.getStatus() == ReportStatus.CREATED) {
            model.addAttribute("status", "CREATED");
            model.addAttribute("message", "Отчёт формируется...");
        } else if (report.getStatus() == ReportStatus.ERROR) {
            model.addAttribute("status", "ERROR");
            model.addAttribute("message", "Ошибка при формировании отчёта");
        } else {
            model.addAttribute("status", "COMPLETED");
            model.addAttribute("reportHtml", report.getContent());
        }

        return "report/report";
    }
}