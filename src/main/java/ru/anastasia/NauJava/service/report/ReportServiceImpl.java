package ru.anastasia.NauJava.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.report.Report;
import ru.anastasia.NauJava.entity.report.ReportStatus;
import ru.anastasia.NauJava.repository.report.ReportRepository;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    /**
     * Репозиторий отчетов
     */
    private final ReportRepository reportRepository;

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    private final SpringTemplateEngine templateEngine;

    @Override
    @Transactional
    public Report createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        return reportRepository.save(report);
    }

    @Override
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new RuntimeException("Не найден отчет с id: " + reportId));

            long startTotal = System.currentTimeMillis();
            LocalDateTime generationTime = LocalDateTime.now();

            try {
                AtomicLong userCount = new AtomicLong(0);
                AtomicLong userTime = new AtomicLong(0);
                AtomicReference<Exception> userError = new AtomicReference<>();

                AtomicReference<List<Contact>> contactsRef = new AtomicReference<>();
                AtomicLong contactTime = new AtomicLong(0);
                AtomicReference<Exception> contactError = new AtomicReference<>();

                Thread userThread = new Thread(() -> {
                    long startUser = System.currentTimeMillis();
                    try {
                        userCount.set(userService.countTotal());
                    } catch (Exception e) {
                        userError.set(e);
                    } finally {
                        userTime.set(System.currentTimeMillis() - startUser);
                    }
                });

                Thread contactThread = new Thread(() -> {
                    long startContact = System.currentTimeMillis();
                    try {
                        contactsRef.set(contactService.findAll());
                    } catch (Exception e) {
                        contactError.set(e);
                    } finally {
                        contactTime.set(System.currentTimeMillis() - startContact);
                    }
                });

                userThread.start();
                contactThread.start();

                userThread.join();
                contactThread.join();

                if (userError.get() != null) {
                    throw new RuntimeException("Ошибка подсчета пользователей: " + userError.get().getMessage(), userError.get());
                }
                if (contactError.get() != null) {
                    throw new RuntimeException("Ошибка загрузки контактов: " + contactError.get().getMessage(), contactError.get());
                }

                long totalTime = System.currentTimeMillis() - startTotal;

                Context context = new Context();
                context.setVariable("userCount", userCount.get());
                context.setVariable("contacts", contactsRef.get());
                context.setVariable("timeUsers", userTime.get());
                context.setVariable("timeContacts", contactTime.get());
                context.setVariable("totalTime", totalTime);
                context.setVariable("generationTime", generationTime);

                String htmlContent = templateEngine.process("report/content", context);

                report.setContent(htmlContent);
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

            } catch (Exception e) {
                report.setStatus(ReportStatus.ERROR);
                String errorContent = "<div class='alert alert-danger'>" +
                        "<h4>Ошибка при формировании отчёта</h4>" +
                        "<p><strong>Причина:</strong> " + escapeHtml(e.getMessage()) + "</p>" +
                        "</div>";
                report.setContent(errorContent);
                reportRepository.save(report);

                throw new RuntimeException("Ошибка генерации отчета: " + e.getMessage());
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Report getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отчет не найден: " + id));
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}