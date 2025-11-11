package ru.anastasia.NauJava.repository.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anastasia.NauJava.entity.report.Report;

/**
 * Репозиторий отчетов
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}