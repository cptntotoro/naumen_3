package ru.anastasia.NauJava.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Статистика дашборда для пользователя
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDashboardStats extends BaseDashboardStats {
    /**
     * Число избранных контактов
     */
    private long favoritesCount;

    /**
     * Ближайшие дни рождения
     */
    private long upcomingBirthdays;
}