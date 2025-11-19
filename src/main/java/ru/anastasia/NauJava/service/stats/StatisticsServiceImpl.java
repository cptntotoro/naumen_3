package ru.anastasia.NauJava.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anastasia.NauJava.dto.stats.AdminDashboardStats;
import ru.anastasia.NauJava.dto.stats.UserDashboardStats;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.user.UserService;

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
        Long contacts = contactService.countTotal();
        Long companies = companyService.countTotal();
        Long favorites = contactService.countFavorites();
        Long birthdays = eventService.countUpcomingBirthdays(14);

        return UserDashboardStats.builder()
                .contactsCount(contacts != null ? contacts : 0L)
                .companiesCount(companies != null ? companies : 0L)
                .favoritesCount(favorites != null ? favorites : 0L)
                .upcomingBirthdays(birthdays != null ? birthdays : 0L)
                .build();
    }

    @Override
    public AdminDashboardStats getAdminDashboardStats() {
        Long users = userService.countTotal();
        Long contacts = contactService.countTotal();
        Long companies = companyService.countTotal();

        return AdminDashboardStats.builder()
                .usersCount(users != null ? users : 0L)
                .contactsCount(contacts != null ? contacts : 0L)
                .companiesCount(companies != null ? companies : 0L)
                .build();
    }

    @Override
    public long getUpcomingBirthdaysCount(int daysAhead) {
        Long result = eventService.countUpcomingBirthdays(daysAhead);
        return result != null ? result : 0L;
    }
}
