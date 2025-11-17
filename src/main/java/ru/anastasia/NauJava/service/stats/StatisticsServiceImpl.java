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
        return UserDashboardStats.builder()
                .contactsCount(contactService.countTotal())
                .companiesCount(companyService.countTotal())
                .favoritesCount(contactService.countFavorites())
                .upcomingBirthdays(getUpcomingBirthdaysCount(14))
                .build();
    }

    @Override
    public AdminDashboardStats getAdminDashboardStats() {
        return AdminDashboardStats.builder()
                .usersCount(userService.countTotal())
                .contactsCount(contactService.countTotal())
                .companiesCount(companyService.countTotal())
                .build();
    }

    @Override
    public Long getUpcomingBirthdaysCount(int daysAhead) {
        return eventService.countUpcomingBirthdays(daysAhead);
    }
}
