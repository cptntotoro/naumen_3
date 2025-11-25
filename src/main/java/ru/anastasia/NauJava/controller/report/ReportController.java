package ru.anastasia.NauJava.controller.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.anastasia.NauJava.entity.report.Report;
import ru.anastasia.NauJava.entity.report.ReportStatus;
import ru.anastasia.NauJava.service.report.ReportService;

@Slf4j
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
        log.info("GET /reports - создание нового отчета");

        try {
            Report report = reportService.createReport();
            log.debug("Отчет создан [ID: {}, статус: {}]", report.getId(), report.getStatus());

            reportService.generateReportAsync(report.getId());
            log.info("Запущено асинхронное формирование отчета [ID: {}]", report.getId());

            return "redirect:/reports/" + report.getId();
        } catch (Exception e) {
            log.error("Ошибка при создании отчета", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public String getReport(@PathVariable Long id, Model model) {
        log.debug("GET /reports/{} - получение отчета", id);

        try {
            Report report = reportService.getReport(id);
            log.debug("Статус отчета [ID: {}]: {}", id, report.getStatus());

            model.addAttribute("reportId", id);

            if (report.getStatus() == ReportStatus.CREATED) {
                model.addAttribute("status", "CREATED");
                model.addAttribute("message", "Отчёт формируется...");
                log.debug("Отчет в процессе формирования [ID: {}]", id);
            } else if (report.getStatus() == ReportStatus.ERROR) {
                model.addAttribute("status", "ERROR");
                model.addAttribute("message", "Ошибка при формировании отчёта");
                log.warn("Ошибка формирования отчета [ID: {}]", id);
            } else {
                model.addAttribute("status", "COMPLETED");
                model.addAttribute("reportHtml", report.getContent());
                log.debug("Отчет успешно сформирован [ID: {}]", id);
            }

            return "report/report";
        } catch (Exception e) {
            log.error("Ошибка при получении отчета [ID: {}]", id, e);
            throw e;
        }
    }
}