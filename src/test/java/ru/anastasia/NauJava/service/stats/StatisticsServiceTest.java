package ru.anastasia.NauJava.service.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anastasia.NauJava.dto.stats.AdminDashboardStats;
import ru.anastasia.NauJava.dto.stats.UserDashboardStats;
import ru.anastasia.NauJava.service.company.CompanyService;
import ru.anastasia.NauJava.service.contact.ContactService;
import ru.anastasia.NauJava.service.event.EventService;
import ru.anastasia.NauJava.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ContactService contactService;

    @Mock
    private CompanyService companyService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    void getUserDashboardStatsSuccess() {
        when(contactService.countTotal()).thenReturn(150L);
        when(companyService.countTotal()).thenReturn(25L);
        when(contactService.countFavorites()).thenReturn(30L);
        when(eventService.countUpcomingBirthdays(30)).thenReturn(5L);

        UserDashboardStats result = statisticsService.getUserDashboardStats();

        assertNotNull(result);
        assertEquals(150L, result.getContactsCount());
        assertEquals(25L, result.getCompaniesCount());
        assertEquals(30L, result.getFavoritesCount());
        assertEquals(5L, result.getUpcomingBirthdays());

        verify(contactService, times(1)).countTotal();
        verify(companyService, times(1)).countTotal();
        verify(contactService, times(1)).countFavorites();
        verify(eventService, times(1)).countUpcomingBirthdays(30);
    }

    @Test
    void getUserDashboardStatsWithZeroValues() {
        when(contactService.countTotal()).thenReturn(0L);
        when(companyService.countTotal()).thenReturn(0L);
        when(contactService.countFavorites()).thenReturn(0L);
        when(eventService.countUpcomingBirthdays(30)).thenReturn(0L);

        UserDashboardStats result = statisticsService.getUserDashboardStats();

        assertNotNull(result);
        assertEquals(0L, result.getContactsCount());
        assertEquals(0L, result.getCompaniesCount());
        assertEquals(0L, result.getFavoritesCount());
        assertEquals(0L, result.getUpcomingBirthdays());
    }

    @Test
    void getAdminDashboardStatsSuccess() {
        when(userService.countTotal()).thenReturn(50L);
        when(contactService.countTotal()).thenReturn(200L);
        when(companyService.countTotal()).thenReturn(40L);

        AdminDashboardStats result = statisticsService.getAdminDashboardStats();

        assertNotNull(result);
        assertEquals(50L, result.getUsersCount());
        assertEquals(200L, result.getContactsCount());
        assertEquals(40L, result.getCompaniesCount());

        verify(userService, times(1)).countTotal();
        verify(contactService, times(1)).countTotal();
        verify(companyService, times(1)).countTotal();
    }

    @Test
    void getAdminDashboardStatsWithZeroValues() {
        when(userService.countTotal()).thenReturn(0L);
        when(contactService.countTotal()).thenReturn(0L);
        when(companyService.countTotal()).thenReturn(0L);

        AdminDashboardStats result = statisticsService.getAdminDashboardStats();

        assertNotNull(result);
        assertEquals(0L, result.getUsersCount());
        assertEquals(0L, result.getContactsCount());
        assertEquals(0L, result.getCompaniesCount());
    }

    @Test
    void getUserDashboardStatsWhenServicesReturnNull() {
        when(contactService.countTotal()).thenReturn(null);
        when(companyService.countTotal()).thenReturn(null);
        when(contactService.countFavorites()).thenReturn(null);
        when(eventService.countUpcomingBirthdays(30)).thenReturn(null);

        UserDashboardStats result = statisticsService.getUserDashboardStats();

        assertNotNull(result);
        assertEquals(0L, result.getContactsCount());
        assertEquals(0L, result.getCompaniesCount());
        assertEquals(0L, result.getFavoritesCount());
        assertEquals(0L, result.getUpcomingBirthdays());
    }

    @Test
    void getAdminDashboardStatsWhenServicesReturnNull() {
        when(userService.countTotal()).thenReturn(null);
        when(contactService.countTotal()).thenReturn(null);
        when(companyService.countTotal()).thenReturn(null);

        AdminDashboardStats result = statisticsService.getAdminDashboardStats();

        assertNotNull(result);
        assertEquals(0L, result.getUsersCount());
        assertEquals(0L, result.getContactsCount());
        assertEquals(0L, result.getCompaniesCount());
    }
}
