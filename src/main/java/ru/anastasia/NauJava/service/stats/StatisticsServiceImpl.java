package ru.anastasia.NauJava.service.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.anastasia.NauJava.dto.stats.AdminDashboardStats;
import ru.anastasia.NauJava.dto.stats.UserDashboardStats;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис компаний
     */
    private final CompanyService companyService;

    /**
     * Сервис событий контактов
     */
    private final EventService eventService;

    @Override
    public UserDashboardStats getUserDashboardStats() {
        log.debug("Получение статистики для пользовательской панели управления");

        long startTime = System.currentTimeMillis();

        try {
            Long contacts = contactService.countTotal();
            Long companies = companyService.countTotal();
            Long favorites = contactService.countFavorites();
            Long birthdays = eventService.countUpcomingBirthdays(30);

            log.trace("Сырые данные статистики пользователя: контакты={}, компании={}, избранные={}, дни рождения={}",
                    contacts, companies, favorites, birthdays);

            UserDashboardStats stats = UserDashboardStats.builder()
                    .contactsCount(contacts != null ? contacts : 0L)
                    .companiesCount(companies != null ? companies : 0L)
                    .favoritesCount(favorites != null ? favorites : 0L)
                    .upcomingBirthdays(birthdays != null ? birthdays : 0L)
                    .build();

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Статистика пользовательской панели сгенерирована за {} мс. " +
                            "Контакты: {}, компании: {}, избранные: {}, дни рождения (30 дней): {}",
                    executionTime, stats.getContactsCount(), stats.getCompaniesCount(),
                    stats.getFavoritesCount(), stats.getUpcomingBirthdays());

            return stats;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при получении статистики пользовательской панели. Время выполнения: {} мс. Причина: {}",
                    errorTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AdminDashboardStats getAdminDashboardStats() {
        log.debug("Получение статистики для административной панели управления");

        long startTime = System.currentTimeMillis();

        try {
            Long users = userService.countTotal();
            Long contacts = contactService.countTotal();
            Long companies = companyService.countTotal();

            log.trace("Сырые данные статистики администратора: пользователи={}, контакты={}, компании={}",
                    users, contacts, companies);

            AdminDashboardStats stats = AdminDashboardStats.builder()
                    .usersCount(users != null ? users : 0L)
                    .contactsCount(contacts != null ? contacts : 0L)
                    .companiesCount(companies != null ? companies : 0L)
                    .build();

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Статистика административной панели сгенерирована за {} мс. " +
                            "Пользователи: {}, контакты: {}, компании: {}",
                    executionTime, stats.getUsersCount(), stats.getContactsCount(),
                    stats.getCompaniesCount());

            return stats;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при получении статистики административной панели. Время выполнения: {} мс. Причина: {}",
                    errorTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public long getUpcomingBirthdaysCount(int daysAhead) {
        log.debug("Получение количества предстоящих дней рождения на {} дней вперед", daysAhead);

        long startTime = System.currentTimeMillis();

        try {
            Long result = eventService.countUpcomingBirthdays(daysAhead);
            long count = result != null ? result : 0L;

            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Количество дней рождения на {} дней вперед: {}, время выполнения: {} мс",
                    daysAhead, count, executionTime);

            return count;

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при получении количества дней рождения на {} дней вперед. Время выполнения: {} мс. Причина: {}",
                    daysAhead, errorTime, e.getMessage(), e);
            throw e;
        }
    }
}