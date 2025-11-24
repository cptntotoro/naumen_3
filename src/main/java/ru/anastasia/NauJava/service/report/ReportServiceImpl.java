package ru.anastasia.NauJava.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Создание нового отчета");

        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        Report savedReport = reportRepository.save(report);

        log.info("Отчет успешно создан. ID: {}, статус: {}",
                savedReport.getId(), savedReport.getStatus());

        return savedReport;
    }

    @Override
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        log.info("Запуск асинхронной генерации отчета ID: {}", reportId);

        return CompletableFuture.runAsync(() -> {
            log.debug("Начало генерации отчета ID: {} в потоке: {}",
                    reportId, Thread.currentThread().getName());

            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> {
                        log.error("Отчет не найден для генерации. ID: {}", reportId);
                        return new RuntimeException("Не найден отчет с id: " + reportId);
                    });

            long startTotal = System.currentTimeMillis();
            LocalDateTime generationTime = LocalDateTime.now();

            log.debug("Генерация отчета ID: {} начата в {}", reportId, generationTime);

            try {
                AtomicLong userCount = new AtomicLong(0);
                AtomicLong userTime = new AtomicLong(0);
                AtomicReference<Exception> userError = new AtomicReference<>();

                AtomicReference<List<Contact>> contactsRef = new AtomicReference<>();
                AtomicLong contactTime = new AtomicLong(0);
                AtomicReference<Exception> contactError = new AtomicReference<>();

                // Параллельное выполнение запросов
                log.debug("Запуск параллельных потоков для сбора данных отчета ID: {}", reportId);

                Thread userThread = new Thread(() -> {
                    String threadName = Thread.currentThread().getName();
                    log.trace("Поток пользователей начал работу: {}", threadName);

                    long startUser = System.currentTimeMillis();
                    try {
                        long count = userService.countTotal();
                        userCount.set(count);
                        log.debug("Сбор данных пользователей завершен. Найдено: {} пользователей, время: {} мс",
                                count, System.currentTimeMillis() - startUser);
                    } catch (Exception e) {
                        userError.set(e);
                        log.error("Ошибка при подсчете пользователей для отчета ID: {}. Причина: {}",
                                reportId, e.getMessage(), e);
                    } finally {
                        userTime.set(System.currentTimeMillis() - startUser);
                        log.trace("Поток пользователей завершил работу: {}", threadName);
                    }
                });

                Thread contactThread = new Thread(() -> {
                    String threadName = Thread.currentThread().getName();
                    log.trace("Поток контактов начал работу: {}", threadName);

                    long startContact = System.currentTimeMillis();
                    try {
                        List<Contact> contacts = contactService.findAll();
                        contactsRef.set(contacts);
                        log.debug("Сбор данных контактов завершен. Найдено: {} контактов, время: {} мс",
                                contacts.size(), System.currentTimeMillis() - startContact);
                    } catch (Exception e) {
                        contactError.set(e);
                        log.error("Ошибка при загрузке контактов для отчета ID: {}. Причина: {}",
                                reportId, e.getMessage(), e);
                    } finally {
                        contactTime.set(System.currentTimeMillis() - startContact);
                        log.trace("Поток контактов завершил работу: {}", threadName);
                    }
                });

                userThread.start();
                contactThread.start();

                log.debug("Ожидание завершения потоков для отчета ID: {}", reportId);
                userThread.join();
                contactThread.join();
                log.debug("Все потоки завершены для отчета ID: {}", reportId);

                if (userError.get() != null) {
                    throw new RuntimeException("Ошибка подсчета пользователей: " + userError.get().getMessage(), userError.get());
                }
                if (contactError.get() != null) {
                    throw new RuntimeException("Ошибка загрузки контактов: " + contactError.get().getMessage(), contactError.get());
                }

                long totalTime = System.currentTimeMillis() - startTotal;

                log.debug("Данные собраны для отчета ID: {}. Пользователи: {}, контакты: {}, общее время: {} мс",
                        reportId, userCount.get(), contactsRef.get().size(), totalTime);

                log.debug("Генерация HTML контента для отчета ID: {}", reportId);

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

                log.info("Отчет успешно сгенерирован. ID: {}, статус: {}, пользователей: {}, контактов: {}, общее время: {} мс",
                        reportId, report.getStatus(), userCount.get(), contactsRef.get().size(), totalTime);

            } catch (Exception e) {
                long errorTime = System.currentTimeMillis() - startTotal;
                log.error("Ошибка генерации отчета ID: {}. Время выполнения: {} мс. Причина: {}",
                        reportId, errorTime, e.getMessage(), e);

                report.setStatus(ReportStatus.ERROR);
                String errorContent = "<div class='alert alert-danger'>" +
                        "<h4>Ошибка при формировании отчёта</h4>" +
                        "<p><strong>Причина:</strong> " + escapeHtml(e.getMessage()) + "</p>" +
                        "</div>";
                report.setContent(errorContent);
                reportRepository.save(report);

                log.warn("Отчет ID: {} переведен в статус ERROR", reportId);

                throw new RuntimeException("Ошибка генерации отчета: " + e.getMessage());
            } finally {
                log.debug("Завершение генерации отчета ID: {} в потоке: {}",
                        reportId, Thread.currentThread().getName());
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Report getReport(Long id) {
        log.debug("Получение отчета по ID: {}", id);

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Отчет не найден при запросе. ID: {}", id);
                    return new RuntimeException("Отчет не найден: " + id);
                });

        log.debug("Отчет найден: ID: {}, статус: {}, длина контента: {} символов",
                report.getId(), report.getStatus(),
                report.getContent() != null ? report.getContent().length() : 0);

        return report;
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