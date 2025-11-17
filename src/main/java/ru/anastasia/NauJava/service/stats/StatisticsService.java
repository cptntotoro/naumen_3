package ru.anastasia.NauJava.service.stats;

import ru.anastasia.NauJava.dto.stats.AdminDashboardStats;
import ru.anastasia.NauJava.dto.stats.UserDashboardStats;

/**
 * Сервис статистики дашбордов
 */
public interface StatisticsService {

    /**
     * Получить статистику для пользовательского дашборда
     *
     * @return Статистика пользователя
     */
    UserDashboardStats getUserDashboardStats();

    /**
     * Получить статистику для админского дашборда
     *
     * @return Статистика администратора
     */
    AdminDashboardStats getAdminDashboardStats();

    /**
     * Получить статистику дней рождения на указанное количество дней вперед
     *
     * @param daysAhead Количество дней для поиска дней рождения
     * @return Количество дней рождения
     */
    Long getUpcomingBirthdaysCount(int daysAhead);
}
