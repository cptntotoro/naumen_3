package ru.anastasia.NauJava.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Базовая статистика дашборда
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDashboardStats {
    /**
     * Число контактов
     */
    private Long contactsCount;

    /**
     * Число компаний
     */
    private Long companiesCount;

    public Long getContactsCount() {
        return contactsCount != null ? contactsCount : 0L;
    }

    public Long getCompaniesCount() {
        return companiesCount != null ? companiesCount : 0L;
    }
}
