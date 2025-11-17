package ru.anastasia.NauJava.service.report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.anastasia.NauJava.entity.contact.Contact;
import ru.anastasia.NauJava.entity.report.Report;
import ru.anastasia.NauJava.entity.report.ReportStatus;
import ru.anastasia.NauJava.repository.report.ReportRepository;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private ContactService contactService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Contact createTestContact() {
        return Contact.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .build();
    }

    private Report createTestReport() {
        return Report.builder()
                .id(1L)
                .status(ReportStatus.CREATED)
                .content("Тестовое содержимое отчета")
                .build();
    }

    @Test
    void createReport_ShouldReturnCreatedReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        Report savedReport = createTestReport();

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        Report result = reportService.createReport();

        assertNotNull(result);
        assertEquals(ReportStatus.CREATED, result.getStatus());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void getReport_WhenReportExists_ShouldReturnReport() {
        Long reportId = 1L;
        Report testReport = createTestReport();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));

        Report result = reportService.getReport(reportId);

        assertNotNull(result);
        assertEquals(testReport.getId(), result.getId());
        verify(reportRepository, times(1)).findById(reportId);
    }

    @Test
    void getReport_WhenReportNotExists_ShouldThrowException() {
        Long nonExistentId = 999L;

        when(reportRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reportService.getReport(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Отчет не найден: " + nonExistentId));
        verify(reportRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void generateReportAsync_WhenValidData_ShouldCompleteSuccessfully() throws Exception {
        Long reportId = 1L;
        Report report = createTestReport();
        List<Contact> contacts = Arrays.asList(createTestContact(), createTestContact());

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userService.countTotal()).thenReturn(5L);
        when(contactService.findAll()).thenReturn(contacts);
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Отчет</html>");
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);
        future.get();

        assertEquals(ReportStatus.COMPLETED, report.getStatus());
        assertNotNull(report.getContent());

        verify(reportRepository, atLeastOnce()).findById(reportId);
        verify(reportRepository, atLeastOnce()).save(any(Report.class));
        verify(userService, times(1)).countTotal();
        verify(contactService, times(1)).findAll();
        verify(templateEngine, times(1)).process(anyString(), any());
    }

    @Test
    void generateReportAsync_WhenReportNotFound_ShouldThrowException() {
        Long nonExistentId = 999L;

        when(reportRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CompletableFuture<Void> future = reportService.generateReportAsync(nonExistentId);

        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertTrue(exception.getCause().getMessage().contains("Не найден отчет с id: " + nonExistentId));
        verify(reportRepository, times(1)).findById(nonExistentId);
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void generateReportAsync_WhenUserServiceFails_ShouldSetErrorStatus() {
        Long reportId = 1L;
        Report report = createTestReport();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userService.countTotal()).thenThrow(new RuntimeException("Ошибка базы данных пользователей"));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);

        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertEquals(ReportStatus.ERROR, report.getStatus());
        assertTrue(report.getContent().contains("Ошибка при формировании отчёта"));
        assertTrue(report.getContent().contains("Ошибка базы данных пользователей"));

        verify(reportRepository, atLeastOnce()).save(any(Report.class));
    }

    @Test
    void generateReportAsync_WhenContactServiceFails_ShouldSetErrorStatus() {
        Long reportId = 1L;
        Report report = createTestReport();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userService.countTotal()).thenReturn(5L);
        when(contactService.findAll()).thenThrow(new RuntimeException("Ошибка базы данных контактов"));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);

        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertEquals(ReportStatus.ERROR, report.getStatus());
        assertTrue(report.getContent().contains("Ошибка при формировании отчёта"));
        assertTrue(report.getContent().contains("Ошибка базы данных контактов"));

        verify(reportRepository, atLeastOnce()).save(any(Report.class));
    }

    @Test
    void generateReportAsync_WhenTemplateEngineFails_ShouldSetErrorStatus() {
        Long reportId = 1L;
        Report report = createTestReport();
        List<Contact> contacts = Collections.singletonList(createTestContact());

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(userService.countTotal()).thenReturn(5L);
        when(contactService.findAll()).thenReturn(contacts);
        when(templateEngine.process(anyString(), any())).thenThrow(new RuntimeException("Ошибка шаблонизатора"));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);

        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertEquals(ReportStatus.ERROR, report.getStatus());
        assertTrue(report.getContent().contains("Ошибка при формировании отчёта"));
        assertTrue(report.getContent().contains("Ошибка шаблонизатора"));

        verify(reportRepository, atLeastOnce()).save(any(Report.class));
    }
}
