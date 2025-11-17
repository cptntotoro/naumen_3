package ru.anastasia.NauJava.service.report;

import ru.anastasia.NauJava.entity.report.Report;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис отчетов
 */
public interface ReportService {

    /**
     * Создать отчет со статусом CREATED
     *
     * @return Отчет со статусом CREATED
     */
    Report createReport();

    /**
     * Сгенерировать отчет асинхронно
     *
     * @param reportId Идентификатор отчета
     */
    CompletableFuture<Void> generateReportAsync(Long reportId);

    /**
     * Получить отчет по идентификатору
     *
     * @param id Идентификатор отчета
     * @return Отчет
     */
    Report getReport(Long id);
}
