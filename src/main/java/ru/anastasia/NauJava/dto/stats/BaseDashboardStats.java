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
    private long contactsCount;

    /**
     * Число компаний
     */
    private long companiesCount;
}
