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
}
