package ru.anastasia.NauJava.dto.stats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Статистика дашборда для администратора
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminDashboardStats extends BaseDashboardStats {
    /**
     * Число пользователей
     */
    private Long usersCount;

    public Long getUsersCount() {
        return usersCount != null ? usersCount : 0L;
    }
}
