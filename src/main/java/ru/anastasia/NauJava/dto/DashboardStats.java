package ru.anastasia.NauJava.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Статистика дашборда
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    /**
     * Число контактов
     */
    private Long contactsCount;

    /**
     * Число компаний
     */
    private Long companiesCount;

    /**
     * Число избранных контактов
     */
    private Long favoritesCount;

    /**
     * Ближайшие дни рождения
     */
    private Long upcomingBirthdays;

    public Long getContactsCount() {
        return contactsCount != null ? contactsCount : 0L;
    }

    public Long getCompaniesCount() {
        return companiesCount != null ? companiesCount : 0L;
    }

    public Long getFavoritesCount() {
        return favoritesCount != null ? favoritesCount : 0L;
    }

    public Long getUpcomingBirthdays() {
        return upcomingBirthdays != null ? upcomingBirthdays : 0L;
    }
}
